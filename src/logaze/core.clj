(ns logaze.core
  (:require [logaze.openapi :as o]
            [logaze.transform :as t]
            [logaze.storage :as s]
            [clojure.set :refer [difference]]
            [clojure.core.async :as a]
            [ring.middleware.cors :refer [wrap-cors]]))

(defn do-scraping []
  (let [parallelism 4
        ;; Track the product codes that need to be enriched
        remaining (atom #{})
        products-raw> (a/chan (* 2 parallelism))
        ;; Close channel only when product codes have been retrieved
        track (fn [item]
                (let [next-remaining (swap! remaining disj (:product-code item))]
                  (when (empty? next-remaining)
                    (a/close! products-raw>)))
                item)
        ;; xf of channel applies after xf of pipeline function
        products-enriched> (a/chan (* 2 parallelism) (comp (map track)
                                                           (map t/transform-attributes)
                                                           (filter :available)
                                                           (map s/clean)))]

    (a/go-loop [n 0
                seen #{}]
      (let [raw-data (o/extract-page-products n)
            new-product-codes (difference (set (map :product-code raw-data)) seen)
            data (filter (fn [d] (new-product-codes (:product-code d))) raw-data)]

        (a/onto-chan!! products-raw> data false)
        (swap! remaining into new-product-codes)

        (when (seq raw-data)
          (recur (inc n) (into seen new-product-codes)))))

    (a/pipeline-blocking
     parallelism products-enriched>
     (map o/enrich-product)
     products-raw>)

    (->> (persistent! (a/<!! (a/reduce conj! (transient []) products-enriched>))))))

(defn scrape-handler [request]
  (when (= "/" (:uri request))
    (a/thread (do-scraping)))
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

  (def page-1 (o/extract-page-products 1))

  (def product-0-page-1 (first page-1))

  (def transformed (s/clean (t/transform-attributes (o/enrich-product product-0-page-1))))

  (-> (o/extract-page-products 1)
      (first)
      (o/enrich-product)
      (t/transform-attributes)
      (s/clean))

  (def l (do-scraping))

  (count l)

  )
