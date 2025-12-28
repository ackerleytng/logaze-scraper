(defproject logaze "0.1.0-SNAPSHOT"
  :uberjar-name "logaze.jar"
  :min-lein-version "2.12.0"
  :dependencies [[org.clojure/clojure "1.12.4"]
                 [ring "1.9.6"]
                 [ring-cors "0.1.13"]
                 [clj-http "3.12.3"]
                 [cheshire "6.1.0"]
                 [org.clojure/core.async "1.8.741"]
                 [com.cognitect.aws/api "0.8.774"]
                 [com.cognitect.aws/endpoints "871.2.40.14"]
                 [com.cognitect.aws/s3 "871.2.40.9"]
                 [environ "1.2.0"]
                 [clojure.java-time "1.4.3"]]
  :ring {:handler logaze.core/handler}
  :plugins [[lein-ring "0.12.6"]
            [lein-eftest "0.6.0"]
            [lein-environ "1.2.0"]]
  :profiles {:dev [:profiles/dev]
             :uberjar {:aot :all}})
