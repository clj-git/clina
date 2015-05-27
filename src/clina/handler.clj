(ns clina.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clina.cljgit :refer :all]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/new" request
    (let [owner (get-in request [:params :owner])
          repository (get-in request [:params :repository])]
      (init-repo owner repository)))
  (route/not-found "Not Found"))

;;暂时先去除csrf保护可以用调试工具调试post请求
(def app
  (wrap-defaults app-routes
    (assoc-in site-defaults [:security :anti-forgery] false)))
