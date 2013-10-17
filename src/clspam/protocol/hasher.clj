(ns clspam.protocol.hasher)

(defprotocol Hasher
  "Interface for hasher plugins.
  These plugins are responsible for hashing tokens."
  (get-name [this] "This method should return the name of this plugin.")
  (init [this] "This method will be called before any hashing takes place.")
  (cleanup [this] "This method will be called before the application shuts down.")
  (hash-tokens [this tokens] "This is the working method. Takes a list of tokens and returns a hash."))