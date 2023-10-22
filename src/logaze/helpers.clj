(ns logaze.helpers
   (:require [clojure.string]))

;; https://stackoverflow.com/questions/18662301/make-clojures-println-thread-safe-in-the-same-way-as-in-java
;; also http://yellerapp.com/posts/2014-12-11-14-race-condition-in-clojure-println.html
(defn safe-println [& more]
  (.write *out* (str (clojure.string/join " " more) "\n"))
  (.flush *out*))
