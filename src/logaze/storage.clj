(ns logaze.storage
  (:require [cheshire.core :refer [generate-string]]
            [amazonica.aws.s3 :as s3]
            [environ.core :refer [env]]
            [java-time.api :as jt]
            [logaze.helpers :as h]))

(defn clean [product]
  (select-keys product
               [:available
                :battery
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

(def cred {:access-key (env :access-key)
           :secret-key (env :secret-key)
           :endpoint (env :endpoint)})

(defn save [data location]
  (s3/put-object cred "logaze" location (generate-string data))
  (h/safe-println (str "Posted " (count data) " entries to " location)))

(defn post [data]
  (let [half (/ (count data) 2)]
    ;; Put data to two places to avoid timeouts on each put
    (save (take half data) "part-0")
    (save (drop half data) "part-1")))

(defn last-scrape-time []
  (-> (s3/get-object-metadata cred "logaze" "part-0")
      :last-modified))
