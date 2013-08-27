(ns clspam.data
  (:require [hyperion.api :refer [save defentity find-by-kind delete-by-key]]))

(def cache (atom []))

(defentity token
  [token]
  [spam :default 0]
  [nonspam :default 0]
  [created-at]
  [updated-at])

(defn- agentize [token]
  (let [agent (agent token)]
    (add-watch agent :db (fn [_key token _old-value new-value]
                           (if (contains? new-value :key)
                             (save new-value)
                             (send token merge (save new-value)))))
    (swap! cache conj agent)
    agent))

(defn find-token [token-value]
  (let [cache-result (filter #(= (:token (deref %)) token-value) @cache)]
    (if (seq cache-result)
      (first cache-result)
      (let [ds-result (find-by-kind :token :filters [:= :token token-value])]
        (if (seq ds-result)
          (agentize (first ds-result))
          (agentize (token :token token-value)))))))