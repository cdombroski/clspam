(ns clspam.hasher_test
  (:require [midje.sweet :refer :all]
            [clspam.hasher :refer :all]))

(facts "hasher facts"
       (fact "Hasher hashes tokens"
             (hasher [[1]] ["hello"]) => [2312643295494852635]
             (hasher [[1]] ["world"]) => [-2910381183370018917])
       (fact "Coefficient map provides multiples"
             (let [base (hasher [[1]] ["hello"])]
               (hasher [[3]] ["hello"]) => (map (partial *' 3) base)
               (hasher [[10]] ["hello"]) => (map (partial *' 10) base)))
       (fact "map's first dimension provides multi-value"
             (let [base (first (hasher [[1]] ["hello"]))]
               (hasher [[1] [3] [10]] ["hello"]) => (map (partial *' base) [1 3 10]))))