(ns logaze.storage
  (:require [clj-http.client :as client]
            [cheshire.core :refer [generate-string]]))

(def storage-api-0 "https://jsonblob.com/api/jsonBlob/381d4455-63af-11ea-ad21-453934360a11")
(def storage-api-1 "https://jsonblob.com/api/jsonBlob/1070684176440901632")

(defn clean [product]
  (select-keys product
               [:battery
                :brand
                :camera
                :display
                :fingerprint-reader
                :graphic-card
                :keyboard
                :memory
                :memory-size
                :memory-soldered
                :model
                :operating-system
                :orig-price
                :percentage-savings
                :price
                :processor
                :processor-brand
                :processor-cache
                :processor-range
                :product-condition
                :product-number
                :resolution
                :screen-has-ips
                :screen-size
                :storage
                :storage-size
                :storage-type
                :touch-screen
                :url
                :warranty
                :weight
                :wlan]))

(defn save [data location]
  (client/put
   location
   {:body (generate-string data) :content-type :json})
  (println (str "Posted " (count data) " entries to " location)))

(defn post [data]
  (let [half (/ (count data) 2)]
    ;; Put data to two places to avoid timeouts on each put
    (save (take half data) storage-api-0)
    (save (drop half data) storage-api-1)))
