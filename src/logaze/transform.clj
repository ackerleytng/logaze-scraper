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
  (when-let [matches (re-find #"(?i)(\d+M)B?" processor-str)]
    (second matches)))

(defn processor-range [processor-str]
  (when-let [matches (re-find #"(i[3579]|Ryzen R?\d+|Celeron|Xeon|Atom|Pentium|A\d+|PRO A\d+)-?"
                              processor-str)]
    (second matches)))

(defn hard-drive-size [hard-drive-str]
  (when-let [matches (re-find #"(?i)([\d\.]+[GT])B?" hard-drive-str)]
    (str (second matches) "B")))

(defn hard-drive-type [s]
  (cond
    (re-find #"(?i)and.*drives" s) "Multi"
    (re-find #"(?i)hard drive" s) "HDD"
    (re-find #"(?i)solid state drive" s) "SSD"
    (re-find #"(?i)embedded multi media card" s) "eMMC"
    (re-find #"RPM" s) "HDD"))

(defn price->float [price]
  (edn/read-string (string/replace price #"[\$,]" "")))

(defn fix-resolution
  "Occasionally the resolution appears as 1920 x 1200 for example, instead of
  1920x1200, this function fixes that"
  [display-type-str]
  (string/replace display-type-str #"(\d+)\s+x\s+(\d+)" "$1x$2"))

(defn product-type [s]
  (when-let [matches (re-find #"(?i)(new|refurbished|scratch and dent)" s)]
    (second matches)))

(defn clean-model [s]
  (string/replace s #"(?i)\s*-\s*(new|refurbished|scratch and dent)" ""))

(defn transform-attributes [attrs]
  (-> attrs
      (extract :part-number :part-number #(string/replace % #"Part Number:  " ""))
      (extract :orig-price :orig-price price->float)
      (extract :price :price price->float)
      (extract :model :product-type product-type)
      (extract :model :model clean-model)
      (extract :display-type :display-type fix-resolution)
      (extract :display-type :screen-size #(edn/read-string (second (re-find #"(\d{2}\.?\d?)" %))))
      (extract :display-type :screen-has-ips #(boolean (re-find #"IPS" %)))
      (extract :display-type :screen-supports-touch #(boolean (re-find #"touch" %)))
      (extract :display-type :resolution #(second (re-find #"(\d+x\d+)" %)))
      (extract :memory :memory-size #(re-find #"\d+GB" %))
      (extract :memory :memory-soldered #(boolean (re-find #"(?i)soldered" %)))
      (extract :hard-drive :hard-drive-size hard-drive-size)
      (extract :hard-drive :hard-drive-type hard-drive-type)
      (extract :processor :processor-brand #(second (re-find #"(?i)(Intel|AMD)" %)))
      (extract :processor :processor-cache processor-cache)
      (extract :processor :processor-range processor-range)))
