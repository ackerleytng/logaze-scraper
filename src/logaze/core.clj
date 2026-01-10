(ns logaze.core
  (:require [logaze.openapi :as o]
            [logaze.transform :as t]
            [logaze.storage :as s]
            [logaze.helpers :as h]
            [clojure.set :refer [difference]]
            [clojure.core.async :as a]
            [medley.core :refer [distinct-by]]
            [ring.middleware.cors :refer [wrap-cors]]
            [environ.core :refer [env]]
            [java-time.api :as jt]))

(def scrape-page-size 40)

(defn- scrape-page [page-size {:keys [page ascending]}]
  (let [product-list (-> (o/raw-page page ascending page-size)
                         (o/extract-page)
                         (o/extract-page-products))]
    (h/safe-println {:info "got page"
                     :page page
                     :ascending ascending
                     :count (count product-list)})
    product-list))

(defn parallel-scrape [page-size inputs]
  (let [parallelism 4

        page-queries> (a/to-chan! inputs)
        products-raw> (a/chan (* 2 parallelism) (distinct-by :product-code))
        products-enriched> (a/chan (* 2 parallelism) (comp
                                                      (map t/transform-attributes)
                                                      (map s/clean)))]

    (a/pipeline-blocking parallelism products-raw>
                         (mapcat #(scrape-page page-size %))
                         page-queries>)

    (a/pipeline-blocking parallelism products-enriched>
                         (map o/enrich-product)
                         products-raw>)

    (a/<!! (a/into [] products-enriched>))))

(defn do-scraping [page-size]
  (let [num-pages (o/num-pages page-size)
        base-inputs (map (fn [page] {:page page :ascending true}) (range 1 (inc num-pages)))
        inputs (if (<= num-pages 800)
                 base-inputs
                 (into base-inputs (map (fn [page] {:page page :ascending false}) (range 1 (inc num-pages)))))]

    ;; Lenovo website can't query more than 800 products (or 20
    ;; pages). See issue https://github.com/ackerleytng/logaze/issues/33.
    (if (> num-pages 20)
      (h/safe-println {:warning "more than 20 pages of products, scraping will be incomplete"}))

    (h/safe-println {:info "all inputs" :inputs inputs})
    (parallel-scrape page-size inputs)))

(defn should-scrape [uri]
  (let [now (jt/zoned-date-time)
        last (jt/zoned-date-time (s/last-scrape-time) "UTC")]
    (h/safe-println {:uri uri
                     :now now
                     :last last})
    (or (and (= "/" uri)
             (jt/after? now (jt/plus last (jt/hours 1))))
        (= (env :force-scrape-path) uri))))

(defn scrape-handler [request]
  (when (should-scrape (:uri request))
    (a/thread (s/post (do-scraping scrape-page-size))))
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
  (def raw-page-1 (o/raw-page 1))

  (o/extract-page-products (o/extract-page (o/raw-page 1)))

  (-> raw-page-1
      (o/extract-page)
      (o/extract-page-products)
      (first)
      (o/enrich-product)
      (t/transform-attributes)
      (s/clean))

  (def scraped (do-scraping scrape-page-size))

  (count scraped)

  (first scraped)

  (s/post scraped)

  )
