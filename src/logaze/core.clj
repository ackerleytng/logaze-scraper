(ns logaze.core
  (:require [logaze.openapi :as o]
            [logaze.transform :as t]
            [logaze.storage :as s]
            [logaze.helpers :as h]
            [clojure.set :refer [difference]]
            [clojure.core.async :as a]
            [ring.middleware.cors :refer [wrap-cors]]
            [environ.core :refer [env]]
            [java-time.api :as jt]))

(defn do-scraping []
  (let [page-size 40
        parallelism 4
        ;; Track the product codes that need to be enriched

        ;; :more-products-coming is a sentinel to indicate that there may be more products to list
        ;; Without :more-products-coming, if enriching products goes more quickly than extracting
        ;; products from a page, scraping may terminate prematurely.
        remaining (atom #{:more-products-coming})
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
                                                           (map s/clean)))
        num-pages (o/num-pages page-size)]

    (a/go-loop [n 0
                ascending true
                seen #{}]
      (let [page (o/extract-page (o/raw-page n ascending page-size))
            product-list (o/extract-page-products page)
            new-product-codes (difference (set (map :product-code product-list)) seen)
            data (filter (fn [d] (new-product-codes (:product-code d))) product-list)]

        (a/onto-chan!! products-raw> data false)
        (swap! remaining into new-product-codes)

        (h/safe-println {:info "got page"
                         :n n
                         :ascending ascending
                         :count (count product-list)
                         :new-count (count new-product-codes)})

        ;; Scrape in ascending order of price, then once there are no
        ;; products, scrape again with descending order of price.
        ;; Need this because Lenovo website can't scrape more than 800
        ;; products. See issue
        ;; https://github.com/ackerleytng/logaze/issues/33. If there
        ;; are more than 1600 products, this workaround will
        ;; fail. Scraping slightly more than half ascending and
        ;; descending in case of off-by-one issues. Not sure if
        ;; num-pages from Lenovo's site is 0 or 1 based.
        (if (and (seq product-list) (< n (/ (+ num-pages 3) 2)))
          (recur (inc n) ascending (into seen new-product-codes))
          (if ascending
            (recur 0 (not ascending) (into seen new-product-codes))
            (swap! remaining disj :more-products-coming)))))

    (a/pipeline-blocking
     parallelism products-enriched>
     (map o/enrich-product)
     products-raw>)

    (->> (persistent! (a/<!! (a/reduce conj! (transient []) products-enriched>))))))

(defn should-scrape [uri]
  (let [now (jt/zoned-date-time)
        last (jt/zoned-date-time (s/last-scrape-time))]
    (println {:uri uri
              :now now
              :last last})
    (or (and (= "/" uri)
             (jt/after? now (jt/plus last (jt/hours 1))))
        (= (env :force-scrape-path) uri))))

(defn scrape-handler [request]
  (when (should-scrape (:uri request))
    (a/thread (s/post (do-scraping))))
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

  (def l (do-scraping))

  (count l)

  (s/post l)

  )
