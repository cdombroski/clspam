(ns clspam.data
  (:require [hyperion.api :refer [save defentity find-by-kind delete-by-key]]
            [chee.datetime :refer [days-ago]]))

(def cache (atom #{}))

(defentity token
  [token]
  [spam :default 0]
  [nonspam :default 0]
  [total-hits :default 0]
  [created-at]
  [updated-at])

(defn- agentize [token]
  (let [agent (agent token)]
    (add-watch agent :db (fn [_key token _old-value new-value] (save new-value)))
    (swap! cache conj agent)
    agent))

(defn find-token [token-value]
  (let [cache-result (filter #(= (:token @%) token-value) @cache)]
    (if (seq cache-result)
      (first cache-result)
      (let [ds-result (find-by-kind :token :filters [:= :token token-value])]
        (if (seq ds-result)
          (agentize (first ds-result))
          (agentize (save (token :token token-value))))))))

(defn evict-tokens
  ([age max-hits] (evict-tokens age max-hits -1))
  ([age max-hits sleep]
    (doseq [hyperion-token (find-by-kind :token :filters [[:<= :total-hits max-hits][:<= :updated-at (days-ago age)]])
            :let [cache-token (find-token (:token hyperion-token))]]
      (swap! cache disj cache-token)
      (delete-by-key (:key hyperion-token)))
    (when (pos? sleep)
      (Thread/sleep sleep)
      (recur age max-hits sleep))))

(defn learn [token class]
  (future
    (send token update-in [class] inc)
    (await token)
    (send token update-in [:total-hits] inc)))

(defn hit [token]
  (send token update-in [:total-hits] inc))