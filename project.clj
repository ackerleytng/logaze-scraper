(defproject logaze "0.1.0-SNAPSHOT"
  :uberjar-name "logaze.jar"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring "1.9.6"]
                 [ring-cors "0.1.13"]
                 [enlive "1.1.6"]
                 [clj-http "3.12.3"]
                 [cheshire "5.11.0"]
                 [org.clojure/core.async "1.6.673"]
                 [slingshot "0.12.2"]
                 [amazonica "0.3.168"]
                 [environ "1.2.0"]
                 [clojure.java-time "1.4.3"]]
  :ring {:handler logaze.core/handler}
  :plugins [[lein-ring "0.12.6"]
            [lein-eftest "0.6.0"]
            [lein-environ "1.2.0"]]
  :profiles {:dev [:profiles/dev]
             :uberjar {:aot :all}})
