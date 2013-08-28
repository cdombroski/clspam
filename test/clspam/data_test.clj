(ns clspam.data-test
  (:require [midje.sweet :refer :all]
            [clspam.data :refer :all]
            [hyperion.api :refer [new-datastore count-by-kind *ds*]]))

(with-state-changes [(around :facts (binding [*ds* (new-datastore :implementation :memory)] ?form))]
  (fact "tokens persist"
    (find-token 12345) => truthy
    (find-token 1234) => truthy
    (learn (find-token 123) :spam) => truthy
    (find-token 12) => truthy
    (learn (find-token 1) :spam) => truthy
    (apply await @cache)
    (count-by-kind :token) => 5)
  (fact "tokens have hyperion metadata"
    (let [found-token (find-token 12345)]
      (:key @found-token) => truthy
      (:created-at @found-token) => truthy
      (:updated-at @found-token) => truthy))
  (fact "we can evict 'old' tokens"
    (reset! cache #{})
    (learn (find-token 12345) :spam) => truthy
    (learn (find-token 1234) :nonspam) => truthy
    (hit (find-token 12345)) => truthy
    (find-token 123)
    (apply await @cache)
    (apply await @cache)
    (count @cache) => 3
    (count-by-kind :token) => 3
    (evict-tokens 0 1)
    (count @cache) => 1
    (count-by-kind :token :filters [:= :token 1234]) => 0))