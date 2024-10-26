(ns logaze.openapi
  (:require [logaze.helpers :as h]
            [clj-http.conn-mgr :refer [make-reusable-conn-manager]]
            [clj-http.core :refer [build-http-client]]
            [clj-http.client :as client]
            [cheshire.core :refer [parse-string generate-string]]
            [clojure.string :as string]))

;; Reusing clients is kinder to the server (also seems to prevent throttling)
(def conn-manager (make-reusable-conn-manager {}))
(def http-client (build-http-client {} false conn-manager))

(defn keywordize [s]
  (->> s
       ;; https://stackoverflow.com/questions/7593969/regex-to-split-camelcase-or-titlecase-advanced
       (re-seq #"(?:[A-Z0-9]+|[A-Z]?[a-z]+)(?=[A-Z]|\b)")
       (string/join "-")
       string/lower-case
       keyword))

(defn classification-edn
  [classification]
  (apply merge
         (map
          (fn [{key :a value :b}]
            {(keywordize key) (string/trim (string/replace value #"<.*?>" ""))})
          classification)))

(defn raw-page
  "Get page number n of the laptops at lenovo's outlet website, with a page-size of page-size, as a string"
  ([n] (raw-page n true 40))
  ([n sort-ascending] (raw-page n sort-ascending 40))
  ([n sort-ascending page-size]
   (let [url "https://openapi.lenovo.com/us/outletus/en/ofp/search/dlp/product/query/get/_tsc"
         ;; This represents the type of products (laptops, desktops, etc)
         ;; Select one of Laptops/Desktops/Workstations/Tablets at
         ;;   https://www.lenovo.com/us/outletus/en/laptops/, view source,
         ;;   and then Ctrl-F for facetId to find this value
         page-filter-id "5dfc6cc3-0105-4ebd-8591-b8bbe8ddaa35"
         ;; Yes, the server wants a double-encoded query, so we have to pass clj-http a string
         query (generate-string {:pageFilterId page-filter-id
                                 :page (str n)
                                 :pageSize (str page-size)
                                 :sorts [(if sort-ascending "priceUp" "priceDown")]})
         params {:accept :json
                 :cookie-policy :standard
                 :query-params {:params query}
                 :headers {:referer "https://www.lenovo.com/"}
                 :connection-manager conn-manager
                 :http-client http-client}]
     (h/safe-println {:info "getting page" :n n :ascending sort-ascending :page-size page-size})
     (:body (client/get url params)))))

(defn num-pages [page-size]
  (-> (raw-page 0 true page-size)
      (parse-string keywordize)
      (get-in [:data :page-count])))

(defn extract-page
  [raw-page]
  (-> raw-page
      (parse-string keywordize)
      (get-in [:data :data])))

(defn detail
  "Get details for a laptop with product-number at lenovo's outlet website"
  [product-number]
  (let [url "https://openapi.lenovo.com/us/outletus/en/product/compare/getCompareData"
        query (generate-string [{:categoryCode "laptops"
                                 :productNumber [product-number]}])
        params {:accept :json
                :cookie-policy :standard
                :query-params {:compareReq query}
                :headers {:referer "https://www.lenovo.com/"}
                :connection-manager conn-manager
                :http-client http-client}]
    (h/safe-println {:info "getting detail" :product-code product-number})
    (:body (client/get url params))))

(defn extract-detail
  [detail]
  (let [parsed (parse-string detail keywordize)]
    (get-in parsed [:data 0 :product-list 0])))

(defn extract-flatten-detail
  [detail]
  (let [product-info (extract-detail detail)]
    (merge (classification-edn (:classification product-info))
           (select-keys product-info
                        [:product-number
                         :url
                         :inventory-status
                         :product-mkt-name]))))

(defn extract-page-products [page]
  (map #(select-keys % [:final-price :web-price :product-code :product-condition :save-percent]) page))

(defn batch-price
  "Get (potentially discounted) price for a laptop with product-number at Lenovo's outlet website.

  :final-price as extracted from the page is sometimes overridden by a lower price from this API call."
  [product-number]
  (let [url "https://openapi.lenovo.com/us/outletus/en/detail/price/batch/get"
        params {:accept :json
                :cookie-policy :standard
                :query-params {:preSelect "1"
                               :mcode [product-number]
                               :configId ""
                               :enteredCode ""}
                :headers {:referer "https://www.lenovo.com/us/en/search"}
                :connection-manager conn-manager
                :http-client http-client}]
    (h/safe-println {:info "getting batch price" :product-code product-number})
    (let [raw (:body (client/get url params))
          price-index 4]
      (get-in (parse-string raw) ["data" product-number price-index]))))

(defn enrich-product [product]
  (let [product-code (:product-code product)
        details (extract-flatten-detail (detail product-code))
        enriched (merge product details)
        batch-price-api-result (batch-price product-code)]
    (if (some? batch-price-api-result)
      (assoc enriched :final-price batch-price-api-result)
      enriched)))
