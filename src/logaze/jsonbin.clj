(ns logaze.jsonbin
  (:require [clj-http.client :as client]
            [cheshire.core :refer [generate-string]]))

(def bin-id "5dac6fed5751f76337fd4ac2")

(defn post [data]
  (client/put
   (str "https://api.jsonbin.io/b/" bin-id)
   {:body (generate-string data) :content-type :json}))
