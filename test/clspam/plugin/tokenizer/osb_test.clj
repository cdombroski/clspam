(ns clspam.plugin.tokenizer.osb-test
  (:require [midje.sweet :refer :all]
            [clspam.plugin.tokenizer.osb :refer :all]))

(fact "This plugin is named \"osb\"."
  (.get-name plugin) => "osb")

(fact "This plugin groups the input into windows of up to length 5, with intermediate words designated by '#'"
  (.tokenize plugin "this is a good test string") =>
    (just ["this+#+#+#+test"
           "is+#+#+test"
           "a+#+test"
           "good+test"
           "is+#+#+#+string"
           "a+#+#+string"
           "good+#+string"
           "test+string"]
    :in-any-order )
  (.tokenize plugin "this") => []
  (.tokenize plugin "") => [])