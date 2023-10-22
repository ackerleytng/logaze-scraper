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

(defn page
  "Get page number n of the laptops at lenovo's outlet website, with a page-size of page-size"
  ([n] (page n 40))
  ([n page-size]
   (let [url "https://openapi.lenovo.com/us/outletus/en/ofp/search/dlp/product/query/get/_tsc"
         ;; This represents the type of products (laptops, desktops, etc)
         ;; Select one of Laptops/Desktops/Workstations/Tablets at
         ;;   https://www.lenovo.com/us/outletus/en/laptops/, view source,
         ;;   and then Ctrl-F for facetId to find this value
         page-filter-id "5dfc6cc3-0105-4ebd-8591-b8bbe8ddaa35"
         ;; Yes, the server wants a double-encoded query, so we have to pass clj-http a string
         query (generate-string {:pageFilterId page-filter-id
                                 :page (str n)
                                 :pageSize (str page-size)})
         params {:accept :json
                 :cookie-policy :standard
                 :query-params {:params query}
                 :headers {:referer "https://www.lenovo.com/"}
                 :connection-manager conn-manager
                 :http-client http-client}]
     (h/safe-println {:info "getting page" :n n})
     (:body (client/get url params)))))

(defn extract-page
  [page]
  (-> page
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

(defn extract-page-products [n]
  (map #(select-keys % [:final-price :web-price :product-code :product-condition :save-percent])
       (extract-page (page n))))

(defn enrich-product [product]
  (let [details (extract-flatten-detail (detail (:product-code product)))]
    (merge product details)))
