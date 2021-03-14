(ns logaze.core
  (:require [logaze.scrape :as s]
            [logaze.extract :as e]
            [logaze.transform :as t]
            [logaze.storage :as storage]
            [clojure.set :refer [union]]
            [clojure.string :as string]
            [clojure.core.async :refer [go]]
            [ring.middleware.cors :refer [wrap-cors]]))

(defn in-stock? [d]
  (let [stock-status (:stock-status d)]
    (not (or (nil? stock-status) (= stock-status "Out of Stock")))))

(defn do-scraping []
  (when-let [num-product-pages (s/num-product-pages)]
    (let [links
          (->> (range num-product-pages)
               (pmap (comp s/laptop-links s/resource-page))
               ;; take-while should work on a lazy infinite range,
               ;;   but the lenovo site seems to be breaking because of a certain product
               ;;   with price ~$1000 (it keeps saying "The page you are looking for
               ;;   cannot be found.") hence this temporary fix
               ;; (take-while seq)
               (apply union)
               (map s/complete-laptop-link))
          extracted
          (pmap (comp e/extract s/resource) links)
          data
          (pmap (fn [e url] (assoc e :url url)) extracted links)
          in-stock (filter in-stock? data)
          transformed (map t/transform-attributes in-stock)]
      (storage/post transformed)
      (println "Posted to storage"))))

(defn scrape-handler [_request]
  (go (do-scraping))
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Done!"})

(def handler
  (wrap-cors
   scrape-handler
   :access-control-allow-origin [#"http://localhost:?\d*/?"
                                 #"https://ackerleytng.github.io/?"]
   :access-control-allow-methods [:get]))

(comment
  (def links
    (->> (range (s/num-product-pages))
         (map (comp s/laptop-links s/resource-page))
         ;; (take-while seq)
         (apply union)
         (map s/complete-laptop-link)))

  (first (filter #(string/includes? % "E14") links))

  (def extracted-random
    (->> (rand-nth links)
         s/resource
         e/extract))

  (def extracted
    (->> "https://www.lenovo.com/us/en/outletus/laptops/thinkpad/thinkpad-e-series/E490s/p/20NGCTR1WW-PF1S6DNH"
         s/resource
         e/extract))

  (def extracted-many (doall (map (fn [l]
                                    (assoc ((comp e/extract s/resource) l)
                                           :url l))
                                  links)))

  (set (filter #(nil? (:stock-status %)) extracted-many))

  (do-scraping)
  )
