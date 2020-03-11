(ns logaze.storage
  (:require [clj-http.client :as client]
            [cheshire.core :refer [generate-string]]))

(def storage-api "https://jsonblob.com/api/jsonBlob/381d4455-63af-11ea-ad21-453934360a11")

(defn post [data]
  (client/put
   storage-api
   {:body (generate-string data) :content-type :json}))
