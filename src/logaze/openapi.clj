(ns logaze.openapi
  (:require [clj-http.conn-mgr :refer [make-reusable-conn-manager]]
            [clj-http.core :refer [build-http-client]]
            [clj-http.client :as client]
            [cheshire.core :refer [parse-string]]
            [clojure.string :as string]))

(defn build-client []
  (let [cm (make-reusable-conn-manager {})
        client (build-http-client {} false cm)]
    {:connection-manager cm
     :http-client client}))

;; Reusing clients is kinder to the server (also seems to prevent throttling)
(def compare-client (build-client))
(def page-client (build-client))

(defn randomly-delay [f]
  (fn [& args]
    (Thread/sleep (max 500 (rand-int 5000)))
    (apply f args)))

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
         query (str "{\"pageFilterId\":\"" page-filter-id
                    "\",\"page\":" n
                    ",\"pageSize\":" page-size "}")
         params (into {:accept :json
                       :cookie-policy :standard
                       :query-params {"params" query}}
                      page-client)]
     (println (string/join " " ["Getting page" url (str n)]))
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
        query (str "[{\"categoryCode\":\"laptops\",\"productNumber\":" [product-number] "}]")
        referer "https://www.lenovo.com/us/outletus/en/compare_product.html'"
        params (into {:accept :json
                      :cookie-policy :standard
                      :query-params {"compareReq" query}
                      :headers {"referer" referer}}
                     compare-client)]
    (println (string/join " " ["Getting detail" url product-number]))
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
  (map #(select-keys % [:final-price :web-price :product-code :product-condition])
       (extract-page (page n))))

(defn enrich-product [product]
  (let [details (extract-flatten-detail (detail (:product-code product)))]
    (merge product details)))
