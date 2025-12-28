(ns logaze.storage
  (:require [cheshire.core :refer [generate-string]]
            [clojure.java.io :as io]
            [cognitect.aws.client.api :as aws]
            [cognitect.aws.credentials :as creds]
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

(def credentials-provider
  (when-not *compile-files*
    (creds/basic-credentials-provider
     {:access-key-id  (env :access-key)
      :secret-access-key  (env :secret-key)})))

(def s3
  (when-not *compile-files*
    (aws/client {:api :s3
                 ;; See https://github.com/cognitect-labs/aws-api/issues/150
                 :region "us-east-1"
                 :credentials-provider credentials-provider
                 :endpoint-override {:hostname (env :endpoint)
                                     :protocol :https}})))

(defn save [data location]
  (aws/invoke s3 {:op :PutObject :request {:Bucket "logaze" :Key location
                                           :Body (.getBytes (generate-string data))}})
  (h/safe-println {:info "posted" :count (count data) :first (generate-string (first data)) :location location}))

(defn post [data]
  (let [half (/ (count data) 2)]
    ;; Put data to two places to avoid timeouts on each put
    (save (take half data) "part-0")
    (save (drop half data) "part-1")))

(defn last-scrape-time []
  (-> (aws/invoke s3 {:op :GetObject :request {:Bucket "logaze" :Key "part-0"}})
      :last-modified))
