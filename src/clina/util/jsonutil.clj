(ns clina.util.jsonutil
  (:require [cheshire.core :as cheshire]
            [ring.util.response :refer [response content-type]]))

(defn json [form]
  (-> form
      cheshire/encode
      response
      (content-type "application/json; charset=utf-8")))
