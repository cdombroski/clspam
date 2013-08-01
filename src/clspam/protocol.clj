(ns clspam.protocol)

(defprotocol tokenizer
  "Interface for tokenizer plugins.
  These plugins will be responsible for breaking the input into individual tokens for scoring"
  (get-name [this] "This method should return the name of this plugin.")
  (init [this] "This method will be called before any tokenizing takes place.")
  (tokenize [this s] "This is the working method. Should return a sequence of tokens.")
  (cleanup [this] "This method will be called before the application shuts down."))