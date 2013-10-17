(ns clspam.data-test
  (:require [midje.sweet :refer :all ]
            [clspam.data :refer :all ]
            [hyperion.api :refer [new-datastore count-by-kind find-by-kind set-ds!]]
            [hyperion.sql.jdbc :refer [execute-mutation]]
            [hyperion.sql.connection :refer [with-connection]]
            [hyperion.sql.query :refer [make-query]]))

(def connection-url "jdbc:sqlite:target/test.sqlite")

(set-ds! (new-datastore :implementation :sqlite :connection-url connection-url))

(with-state-changes
  [(before :facts (do
                    (create-database connection-url)
                    (reset! cache #{})
                    (reset! shutting-down? false)
                    (.start (Thread. sync-updates))))
   (after :facts (do
                   (with-connection connection-url
                     (execute-mutation
                       (make-query "drop table hash_value")))
                   (reset! shutting-down? true)))]
  (fact "hashes persist"
    (find-hash 12345 "") => truthy
    (find-hash 1234 "") => truthy
    (learn (find-hash 123 "") :spam )
    (deref (find-hash 123 "")) => (contains {:spam 1 :total-hits 1})
    (find-hash 12 "") => truthy
    (learn (find-hash 1 "") :nonspam )
    (deref (find-hash 1 "")) => (contains {:nonspam 1 :total-hits 1})
    (loop []
      (when (seq @update-queue)
        (Thread/sleep 500)
        (recur)))
    (find-by-kind :hash-value ) => (five-of (contains {:kind "hash-value"})))
  (fact "hashs have hyperion metadata"
    (let [found-hash (find-hash 12345 "")]
      (:key @found-hash) => truthy
      (:created-at @found-hash) => truthy
      (:updated-at @found-hash) => truthy))
  (fact "we can evict 'old' hashs"
    (learn (find-hash 12345 "") :spam ) => truthy
    (learn (find-hash 1234 "") :nonspam ) => truthy
    (hit (find-hash 12345 "")) => truthy
    (find-hash 123 "")
    (loop []
      (when (seq @update-queue)
        (Thread/sleep 500)
        (recur)))
    (map deref @cache) => (three-of (contains {:kind "hash-value"}))
    (find-by-kind :hash-value ) => (three-of (contains {:kind "hash-value"}))
    (evict-hashes 0 1)
    (map deref @cache) => (one-of (contains {:kind "hash-value"}))
    (find-by-kind :hash-value :filters [:= :hash 1234]) => []))
