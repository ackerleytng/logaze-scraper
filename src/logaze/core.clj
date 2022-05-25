(ns logaze.core
  (:require [logaze.openapi :as o]
            [logaze.transform :as t]
            [logaze.storage :as s]
            [clojure.set :refer [union]]
            [clojure.core.async :refer [go]]
            [ring.middleware.cors :refer [wrap-cors]]))

(defn do-scraping []
  (->> (range)
       (pmap o/extract)
       (take-while seq)
       (apply union)
       (pmap t/transform-attributes)
       (filter :available)
       (pmap s/clean)
       (s/post))
  (println "Posted to storage!"))

(defn scrape-handler [_request]
  (go (do-scraping))
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Done!"})

(def handler
  (wrap-cors
   scrape-handler
   :access-control-allow-origin [#"http://localhost:?\d*/?"
                                 #"https://ackerleytng.github.io/?"]
   :access-control-allow-methods [:get]))

(comment
  (o/extract-detail (o/detail "81VU00D5US"))

  (def all-page-1 (o/extract 1))

  (def page-1 (o/extract-page (o/page 1)))

  (def product-0-page-1 (first page-1))

  (def extracted (o/extract-product-full product-0-page-1))

  (def transformed (t/transform-attributes extracted))

  (do-scraping)
  )
