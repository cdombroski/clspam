(ns clspam.data-test
  (:require [midje.sweet :refer :all]
            [clspam.data :refer :all]
            [hyperion.api :refer [new-datastore count-by-kind *ds*]]))

(with-state-changes [(around :facts (binding [*ds* (new-datastore :implementation :memory)] ?form))]
  (fact "new tokens don't persist"
    (find-token 12345) => truthy
    (find-token 1234) => truthy
    (find-token 123) => truthy
    (find-token 12) => truthy
    (find-token 1) => truthy
    (count-by-kind :token) => 0)
  (fact "tokens persist after update"
    (find-token 12345) => truthy
    (find-token 1234) => truthy
    (send (find-token 123) update-in [:spam] inc) => truthy
    (find-token 12) => truthy
    (send (find-token 1) update-in [:spam] inc) => truthy
    (await (find-token 123) (find-token 1))
    (count-by-kind :token) => 2)
  (fact "tokens get hyperion metadata after persisting"
    (let [found-token (find-token 12345)]
      (send found-token update-in [:spam] inc) => truthy
      (await found-token)
      (:key @found-token) => truthy
      (:created-at @found-token) => truthy
      (:updated-at @found-token) => truthy)))