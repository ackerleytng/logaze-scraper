(ns logaze.test-helpers
  (:require [net.cgrand.enlive-html :as html]))

(defn resource-filename
  [filename]
  (html/html-resource
   (java.io.StringReader.
    (slurp filename))))
