(ns logaze.extract
  "Extraction functions, given resource at individual model's page, like 'https://www.lenovo.com/us/en/outletus/laptops/ideapad/ideapad-300-series/IdeaPad-330-15-Intel/p/81DJ0007US'"
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as string]))

;; Helper functions

(defn clean-content [element]
  (some->> element
           first :content first
           string/trim))

;; Standalone info

(defn model [res]
  {:model (clean-content (html/select res [:h2.singleModelTitle]))})

(defn part-number [res]
  {:part-number (clean-content (html/select res [:div.partNumber]))})

(defn stock-status [res]
  (let [c (clean-content
           (html/select
            res
            [:div.pricingSummary-shipping.deliveryTimeItemForsessionStorage :span.rci-msg]))]
    {:stock-status c}))

;; Relating to prices

(defn- price-type [pricing-info]
  (->> pricing-info
       :attrs
       :data-pricetypevalue))

(defn- list-price? [pricing-info]
  (when-let [price-type (price-type pricing-info)]
    (string/includes? price-type "LISTPRICE")))

(defn- web-price? [pricing-info]
  (when-let [price-type (price-type pricing-info)]
    (string/includes? price-type "WEBPRICE")))

(defn prices [res]
  (let [prices (html/select res [:dd.saleprice])
        list-price
        (clean-content
         (html/select (filter list-price? prices)
                      [:strike]))
        web-price
        (clean-content (filter web-price? prices))]
    {:orig-price list-price
     :price web-price}))

;; Relating to all the attributes

(defn- attr-keyword [attribute]
  (keyword (.toLowerCase (string/replace attribute #" " "-"))))

(defn- attribute [attribute-info]
  {(attr-keyword (clean-content (html/select attribute-info [:h4])))
   (clean-content (html/select attribute-info [:p]))})

(defn attributes [res]
  (map attribute (html/select res [:div.configuratorItem-mtmTable-text])))

;; Aggregating everything above

(defn extract [res]
  (merge
   (apply merge (attributes res))
   (prices res)
   (model res)
   (part-number res)
   (stock-status res)))
