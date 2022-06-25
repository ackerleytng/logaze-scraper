(ns logaze.transform
  (:require [clojure.string :as string]
            [clojure.edn :as edn]))

(defn- extract [data key new-key fn]
  (assoc
   data new-key
   (when-let [e (key data)]
     (fn e))))

;; Individual functions

(defn processor-cache [processor-str]
  (when-let [matches (re-find #"(?i)\(.*?(\d+ ?M)B?" processor-str)]
    (string/replace (second matches) " " "")))

(defn processor-range [processor-str]
  (let [cleaned (string/replace processor-str #"[^A-Za-z0-9\- ]" "")
        regex #"(i[3579]|Ryzen R?\d+|Celeron|Athlon|Xeon|Atom|Pentium|A\d+|PRO A\d+|R\d)-?"]
    (when-let [matches (re-find regex cleaned)]
      (second matches))))

(defn processor-brand [processor-str]
  (re-find #"(?i)Intel|AMD|MediaTek" processor-str))

(defn storage-size [storage-str]
  (when-let [matches (re-find #"(?i)([\d\.]+ ?[GT])B?" storage-str)]
    (string/replace (str (second matches) "B") " " "")))

(defn storage-type [s]
  (cond
    (re-find #"(?i)and.*drives" s) "Multi"
    (re-find #"(?i)hard drive" s) "HDD"
    (re-find #"(?i)solid state" s) "SSD"
    (re-find #"(?i)embedded multi media card" s) "eMMC"
    (re-find #"RPM" s) "HDD"
    :else (when-let [match (re-find #"(?i)(?:hdd|ssd|emmc)" s)]
            (string/upper-case match))))

(defn memory-size [memory-str]
  (when-let [match (re-find #"\d+ ?GB" memory-str)]
    (string/replace match " " "")))

(defn resolution
  [display-str]
  (when-let [match (re-find #"\d+\s*x\s*\d+" display-str)]
    (string/replace match " " "")))

(defn product-type [s]
  (when-let [matches (re-find #"(?i)(new|refurbished|scratch and dent)" s)]
    (second matches)))

(defn transform-attributes [attrs]
  (-> attrs
      (extract :web-price :orig-price #(Float/parseFloat %))
      (extract :final-price :price #(Float/parseFloat %))
      (extract :product-mkt-name :model string/trim)
      (extract :display :screen-size #(edn/read-string (second (re-find #"(\d{2}\.?\d?)" %))))
      (extract :display :screen-has-ips #(boolean (re-find #"IPS" %)))
      (extract :display :resolution resolution)
      (extract :memory :memory-size memory-size)
      (extract :memory :memory-soldered #(boolean (re-find #"(?i)soldered" %)))
      (extract :storage :storage-size storage-size)
      (extract :storage :storage-type storage-type)
      (extract :processor :processor-brand processor-brand)
      (extract :processor :processor-cache processor-cache)
      (extract :processor :processor-range processor-range)
      (extract :product-condition :product-condition string/trim)
      (extract :url :url #(str "https://www.lenovo.com/us/outletus/en" %))
      (extract :inventory-status :available {2 false 1 true})
      (extract :save-percent :percentage-savings #(Float/parseFloat %))))
