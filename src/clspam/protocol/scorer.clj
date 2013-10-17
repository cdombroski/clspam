(ns clspam.protocol.scorer)

(defprotocol Scorer
  "Interface for scoring plugins"
  (get-name [this] "This method should return the name of this plugin.")
  (init [this] "This method will be called before any hashing takes place.")
  (cleanup [this] "This method will be called before the application shuts down.")
  (score-tokens [this hashes]))