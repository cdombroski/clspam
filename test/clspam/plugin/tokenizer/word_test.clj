(ns clspam.plugin.tokenizer.word-test
  (:require [midje.sweet :refer :all]
            [clspam.plugin.tokenizer.word :refer :all]))

(fact "This plugin is named \"word\"."
  (.get-name plugin) => "word")

(fact "This plugin splits input on whitespace"
  (.tokenize plugin "test string") => ["test" "string"]
  (.tokenize plugin "test") => ["test"]
  (.tokenize plugin "") => [""])