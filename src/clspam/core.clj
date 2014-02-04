(ns clspam.core
  (:gen-class )
  (:import [java.io PushbackReader])
  (:require [clspam.protocol.scorer :as scorer]
            [clspam.protocol.hasher :as hasher]
            [clspam.data :as data]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [aleph.tcp :as tcp]
            [clojure.tools.logging :as log]
            [hyperion.api :as hyper]))

(def config
  "Holds configuration data.
  Starts with default values and has provided values merged in once read."
  {
    :listen-port 10024
    :listen-address nil
    :lmtp-port nil
    :lmtp-address "127.0.0.1"
    :token-regex #"[[:graph:]]+"
    :token-hasher nil
    :scoring-algorithm nil
    :address-extension-separator nil
    :token-file nil})

(defn -main [args]
  (if (seq args)
    (with-open [infile (PushbackReader. (io/reader (first args)))]
      (def config (merge config (edn/read infile)))
      (if (every? val (select-keys config [:lmtp-port :token-hasher :scoring-algorithm :token-file ]))
        (let [hasher (.newInstance (Class/forName (:token-hasher config)))
              scorer (.newInstance (Class/forName (:scoring-algorithm config)))]
          (if (and
                (fn? hasher)
                (fn? scorer))
            (do
              (when-not (.exists (io/file (:token-file config)))
                (data/create-database (str "jdbc:sqlite:" (:token-file config))))
              (binding [hyper/*ds* (hyper/new-datastore
                                     :implementation :sqlite :connection-url (str "jdbc:sqlite:" (:token-file config)))]
                (.start (Thread. data/sync-updates))
                ;;TODO: LOGIC!
                nil))
            (log/error "Invalid token-hasher or scoring-algorithm specified")))
        (log/error "Invalid config file"))))
  (log/error "Config file must be provided"))