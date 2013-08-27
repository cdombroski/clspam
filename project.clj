(defproject clspam "0.1.0-SNAPSHOT"
  :description "Clojure spam filter"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [hyperion/hyperion-api "3.6.0"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]
                   :plugins [[lein-midje "3.0.1"]]}
             :1.4 [:dev {:dependencies [[org.clojure/clojure "1.4.0"]]}]
             :1.5 [:dev {:dependencies [[org.clojure/clojure "1.5.0"]]}]
             :1.5.1 [:dev {:dependencies [[org.clojure/clojure "1.5.1"]]}]}
  :main clspam.core)