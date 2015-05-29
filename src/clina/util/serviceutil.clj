(ns clina.util.serviceutil
  (:require [cheshire.core :as cheshire]
            [clojure.string :as str]
            [ring.util.response :refer [response content-type]]))

(defn json [form]
  (-> form
      cheshire/encode
      response
      (content-type "application/json; charset=utf-8")))

(defmacro fn-name
  [f]
  `(-> ~f var meta :name str))

(defn get-fn-name
  [fn]
  (nth (str/split (str (type fn)) #"\$") 1))

(defn arraylist2vector
  "convert java.util.ArrayList -> vector"
  [arraylist]
  (into [] (java.util.ArrayList. arraylist)))