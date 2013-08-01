(ns clspam.plugin.tokenizer.word
  (:require [clspam.protocol :refer [tokenizer]]
            [clojure.string :refer [split]]))

(def plugin
  (reify tokenizer
    (get-name [_] "word")
    (init [_] nil)
    (cleanup [_] nil)
    (tokenize [_ s]
      (split s #"\s"))))