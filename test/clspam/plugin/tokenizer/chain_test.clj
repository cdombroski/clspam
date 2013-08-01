(ns clspam.plugin.tokenizer.chain-test
  (:require [midje.sweet :refer :all]
            [clspam.plugin.tokenizer.chain :refer :all]))

(fact "This plugin is named \"chain\"."
  (.get-name plugin) => "chain")

(fact "This plugin groups the input into groups of 2 separated by '+'"
  (.tokenize plugin "this is a test string") => ["this+is" "is+a" "a+test" "test+string"]
  (.tokenize plugin "this") => nil
  (.tokenize plugin "") => nil)