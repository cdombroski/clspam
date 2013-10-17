(ns clspam.data
  (:require [hyperion.api :refer [save defentity find-by-kind delete-by-key]]
            [chee.datetime :refer [days-ago]]
            [hyperion.sql.jdbc :refer [execute-mutation]]
            [hyperion.sql.connection :refer [with-connection]]
            [hyperion.sql.query :refer [make-query]]))

(clojure.lang.RT/loadClassForName "org.sqlite.JDBC")

(def cache (atom #{}))

(def update-queue (ref clojure.lang.PersistentQueue/EMPTY))
(def shutting-down? (atom false))

(defn create-database [conn-url]
  (with-connection conn-url
    (execute-mutation
      (make-query
        (str
          "create table if not exists hash_value ("
          "id integer primary key,"
          "hash integer,"
          "user varchar(35),"
          "spam integer,"
          "nonspam integer,"
          "total_hits integer,"
          "created_at timestamp,"
          "updated_at timestamp)")))))

(defentity hash-value
  [hash]
  [user]
  [spam :default 0]
  [nonspam :default 0]
  [total-hits :default 0]
  [created-at]
  [updated-at])

(defn find-hash [hash user]
  (let [cache-result (filter #(and (= (:hash @%) hash) (= (:user @%) user)) @cache)]
    (if (seq cache-result)
      (first cache-result)
      (let [ds-result (find-by-kind :hash-value :filters [[:= :hash hash] [:= :user user]])
            final-result (if (seq ds-result)
                           (ref (first ds-result))
                           (ref (save (hash-value :hash hash :user user))))]
        (swap! cache conj final-result)
        final-result))))

(defn evict-hashes
  ([age max-hits] (evict-hashes age max-hits -1))
  ([age max-hits sleep]
    (doseq [hyperion-hash (find-by-kind :hash-value :filters [[:<= :total-hits max-hits] [:<= :updated-at (days-ago age)]])
            :let [cache-hash (find-hash (:hash hyperion-hash) (:user hyperion-hash))]]
      (swap! cache disj cache-hash)
      (delete-by-key (:key hyperion-hash)))
    (when (pos? sleep)
      (Thread/sleep sleep)
      (recur age max-hits sleep))))

(defn learn [hash class]
  (dosync
    (alter hash update-in [class] inc)
    (alter hash update-in [:total-hits ] inc)
    (alter update-queue conj hash)))

(defn hit [hash]
  (dosync
    (alter hash update-in [:total-hits ] inc)
    (alter update-queue conj hash)))

(defn sync-updates []
  (dosync
    (when-let [hash (peek @update-queue)]
      (alter update-queue pop)
      (save @hash)))
  (when-not @shutting-down?
    (recur)))
