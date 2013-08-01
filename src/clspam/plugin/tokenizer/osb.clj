(ns clspam.plugin.tokenizer.osb
  (:require [clspam.protocol :refer [tokenizer]]
            [clojure.string :refer [split join]]))

(def plugin
  (reify tokenizer
    (get-name [_] "osb")
    (init [_] nil)
    (cleanup [_] nil)
    (tokenize [_ s]
      (let [words (split s #"\s")
            windows (partition 5 1 words)]
        (for [window windows
              x (range 2 6)]
          (join "+" (map-indexed
                      #(if (or (= 0 %1) (= (- x 1) %1))
                                     %2
                                     "#")
                      (take-last x window))))))))