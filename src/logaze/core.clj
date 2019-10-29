(ns logaze.core
  (:require [logaze.scrape :as s]
            [logaze.extract :as e]
            [logaze.transform :as t]
            [logaze.jsonbin :as jsonbin]
            [clojure.set :refer [union]]
            [clojure.core.async :refer [go]]
            [ring.middleware.cors :refer [wrap-cors]]))

(defn do-scraping []
  (let [links
        (->> (range)
             (pmap (comp s/laptop-links s/resource-page))
             (take-while seq)
             (apply union)
             (map s/complete-laptop-link))
        extracted
        (pmap (comp e/extract s/resource) links)
        data
        (pmap (fn [e url] (assoc e :url url)) extracted links)
        in-stock (filter :model data)
        transformed (map t/transform-attributes in-stock)]
    (do (jsonbin/post transformed)
        (println "Posted to jsonbin"))))

(defn scrape-handler [request]
  (do
    (go (do-scraping))
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body "Done!"}))

(def handler
  (wrap-cors
   scrape-handler
   :access-control-allow-origin [#"http://localhost:?\d*/?"
                                 #"https://ackerleytng.github.io/?"]
   :access-control-allow-methods [:get]))

(comment
  (def links
    (->> (range)
         (map (comp s/laptop-links s/resource-page))
         (take-while seq)
         (apply union)
         (map s/complete-laptop-link)))

  (def extracted
    (->> (repeatedly 5 #(rand-nth links))

         (filter :model)))

  (map (juxt t/transform-attributes identity) extracted)

  (t/transform-attributes (second extracted))

  (do-scraping)
  )
