(ns logaze.scrape
  (:require [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [clojure.string :as string]))

(defn resource-page
  "Gets resource based on page number num"
  [num]
  (let [q-params {"q" ":price-asc"
                  "pageSize" ""
                  "page" num}]
    (println (str "resource-page " num))
    (html/html-resource
     (java.io.StringReader.
      (:body
       (client/get
        "https://www.lenovo.com/us/en/outletus/laptops/c/LAPTOPS"
        {:query-params q-params
         :throw-exceptions false}))))))

(defn resource
  "Gets resource at url"
  [url]
  (println (str "resource " url))
  (html/html-resource
   (java.io.StringReader.
    (:body (client/get url)))))

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
