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
  (when-let [matches (re-find #"(i[3579]|Ryzen \d+|Celeron)" processor-str)]
    (second matches)))

(defn hard-drive-size [hard-drive-str]
  (when-let [matches (re-find #"(?i)(\d+(?:GB|TB))" hard-drive-str)]
    (second matches)))

(defn price->float [price]
  (edn/read-string (string/replace price #"[\$,]" "")))

(defn transform-attributes [attrs]
  (-> attrs
      (extract :keyboard :keyboard #(string/replace % #"Keyboard - " ""))
      (extract :part-number :part-number #(string/replace % #"Part Number:  " ""))
      (extract :orig-price :orig-price price->float)
      (extract :price :price price->float)
      (extract :model :refurbished #(boolean (re-find #"(?i)refurbished" %)))
      (extract :display-type :screen-size #(edn/read-string (second (re-find #"(\d{2}.\d)" %))))
      (extract :display-type :screen-has-ips #(boolean (re-find #"IPS" %)))
      (extract :display-type :screen-supports-touch #(boolean (re-find #"touch" %)))
      (extract :display-type :resolution #(second (re-find #"(\d+x\d+)" %)))
      (extract :memory :memory-size #(re-find #"\d+GB" %))
      (extract :memory :memory-soldered #(boolean (re-find #"(?i)soldered" %)))
      (extract :hard-drive :hard-drive-size hard-drive-size)
      (extract :hard-drive :hard-drive-type
               #(second (re-find
                         #"(?i)(embedded Multi Media Card|Solid State Drive|Hard Drive)" %)))
      (extract :processor :processor-brand #(second (re-find #"(?i)(Intel|AMD)" %)))
      (extract :processor :processor-cache processor-cache)
      (extract :processor :processor-range processor-range)))
