(ns logaze.storage
  (:require [clj-http.client :as client]
            [cheshire.core :refer [generate-string]]))

(def storage-api "https://jsonblob.com/api/jsonBlob/381d4455-63af-11ea-ad21-453934360a11")

(defn clean [product]
  (select-keys product
               [:battery
                :brand
                :camera
                :display
                :fingerprint-reader
                :graphics
                :keyboard
                :memory
                :memory-size
                :memory-soldered
                :model
                :operating-system
                :orig-price
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

(defn post [data]
  (client/put
   storage-api
   {:body (generate-string data) :content-type :json}))
