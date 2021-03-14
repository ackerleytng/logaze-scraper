(ns logaze.scrape
  (:require [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [slingshot.slingshot :refer [try+]]))

(defn resource-page
  "Gets resource based on page number num"
  [num]
  (let [q-params {"q" ":price-asc"
                  "pageSize" ""
                  "page" num}]
    (println (str "Getting resource-page " num))
    (html/html-resource
     (java.io.StringReader.
      (:body
       (client/get
        "https://www.lenovo.com/us/en/outletus/laptops/c/LAPTOPS"
        {:query-params q-params
         :throw-exceptions false
         :cookie-policy :none}))))))

(defn num-product-pages
  "Using the first product page, grab the number of products found and
  then determine how many pages there should be"
  []
  (let [products-per-page 8
        res (resource-page 0)]
    (if-let [total-results-div (first (html/select res [:div.totalResults]))]
      (let [n-results
            (->> total-results-div
                 :content
                 (filter string?)
                 (apply str)
                 (re-find #"(?i)(\d+) products found")
                 second
                 Integer.)]
        (int (Math/ceil (/ n-results products-per-page))))
      (println "Can't get totalResults - maybe we're being blocked"))))

(defn resource
  "Gets resource at url"
  [url]
  (println (str "Getting resource " url))
  (html/html-resource
   (java.io.StringReader.
    (try+
     (:body (client/get url {:throw-exceptions false
                             :cookie-policy :none}))
     (catch Object _
       (println (str "Unexpected error accessing " url))
       "")))))

(defn laptop-links
  "Given a resource res of a page with the grid of laptops, like
   'https://www.lenovo.com/us/en/outletus/laptops/c/LAPTOPS?q=%3Aprice-asc&page=0&pageSize='
   return a set of all the links to laptops on the page.

  Returns an empty set if there are no links on this page (there are no more laptops available)"
  [res]
  (->> (html/select res [:a.facetedResults-cta])
       (map (comp :href :attrs))
       set))

(defn complete-laptop-link
  "Links returned from laptop-links are relative, this makes it absolute"
  [link]
  (str "https://www.lenovo.com" link))
