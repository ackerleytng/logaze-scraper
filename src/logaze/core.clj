(ns logaze.core
  (:require [logaze.openapi :as o]
            [logaze.transform :as t]
            [logaze.storage :as s]
            [clojure.set :refer [union]]
            [clojure.core.async :refer [go]]
            [ring.middleware.cors :refer [wrap-cors]]))

(defn distinct-by-product-code [v]
  (map first (vals (group-by :product-code v))))

(defn do-scraping []
  (->> (range)
       (pmap o/extract-page-products)
       (take-while seq)
       (apply union)
       (distinct-by-product-code)
       (pmap (comp t/transform-attributes o/enrich-product))
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

  (def page-1 (o/extract-page (o/page 1)))

  (def product-0-page-1 (first page-1))

  (def transformed (t/transform-attributes (o/enrich-product product-0-page-1)))

  (do-scraping)
  )
