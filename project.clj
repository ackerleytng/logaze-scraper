(defproject logaze "0.1.0-SNAPSHOT"
  :uberjar-name "logaze.jar"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring "1.9.5"]
                 [ring-cors "0.1.13"]
                 [enlive "1.1.6"]
                 [clj-http "3.12.3"]
                 [cheshire "5.11.0"]
                 [org.clojure/core.async "1.5.648"]
                 [slingshot "0.12.2"]]
  :ring {:handler logaze.core/handler}
  :plugins [[lein-ring "0.12.6"]]
  :profiles {:uberjar {:aot :all}})
