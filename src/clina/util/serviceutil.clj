(ns clina.util.serviceutil
  (:require [cheshire.core :as cheshire]
            [ring.util.response :refer [response content-type]]))

(defn json [form]
  (-> form
      cheshire/encode
      response
      (content-type "application/json; charset=utf-8")))

(defmacro fn-name
  [f]
  `(-> ~f var meta :name str))