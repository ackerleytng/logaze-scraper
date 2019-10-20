(defproject logaze "0.1.0-SNAPSHOT"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring "1.7.1"]
                 [enlive "1.1.6"]
                 [clj-http "3.10.0"]
                 [cheshire "5.9.0"]
                 [org.clojure/core.async "0.4.500"]]
  :ring {:handler logaze.core/handler}
  :plugins [[lein-ring "0.12.5"]])
