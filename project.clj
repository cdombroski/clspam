(defproject clspam "0.1.0-SNAPSHOT"
  :description "Clojure spam filter"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [hyperion/hyperion-sqlite "3.7.1"]
                 [chee "1.0.0"]
                 [aleph "0.3.0"]
                 [org.clojure/tools.logging "0.2.6"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]
                   :plugins [[lein-midje "3.0.1"]]}
             :1.5 [:dev {:dependencies [[org.clojure/clojure "1.5.0"]]}]
             :1.5.1 [:dev {:dependencies [[org.clojure/clojure "1.5.1"]]}]}
  :main clspam.core
  :aot [clspam.protocol.hasher clspam.protocol.scorer])