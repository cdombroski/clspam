(ns clspam.plugin.tokenizer.chain
  (:require [clspam.protocol :refer [tokenizer]]
            [clojure.string :refer [split]]))

(def plugin
  (reify tokenizer
    (get-name [_] "chain")
    (init [_] nil)
    (cleanup [_] nil)
    (tokenize [_ s]
      (let [words (split s #"\s")
            chain (fn chain [first-words second-words]
                    (if (seq second-words)
                      (cons (str (first first-words) "+" (first second-words))
                        (lazy-seq (chain (next first-words) (next second-words))))))]
        (if (> (count words) 1)
          (chain words (next words)))))))