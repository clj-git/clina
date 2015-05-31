(ns clina.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response content-type]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clina.view.layout :as layout]
            [clina.util.serviceutil :refer :all]
            [clina.util.git.cljgit :refer :all]
            [clina.controller.view-repository-controller :refer :all]))

(defroutes app-routes
  (GET "/" [] "Hello Clina")
  (GET "/new" []
    (layout/render "newrepo" {:title "just have fun"}))
  (POST "/new" request
    (let [owner (get-in request [:params :owner])
          repository (get-in request [:params :repository])]
      (json (init-repo owner repository))))
  (context "/:owner/:repository" []
    (GET "/"
         request
      (repo-viewer request view-repo))
    (GET "/tree/:revision"
         request
      (repo-viewer request view-repo))
    (GET "/tree/:revision/*"
         request
      (repo-viewer request view-repo))
    (GET "/commit/:commithash"
         request
      (repo-viewer request view-repo-commit))
    ;;http://localhost:3000/root/hehehe/commits/jihui_dev?page=3
    (GET "/commits/:revision"
         request
      (repo-viewer request view-repo-commits))
    ;;http://localhost:3000/root/hehehe/commits/jihui_dev/hehe?page=1
    (GET "/commits/:revision/*"
         request
      (repo-viewer request view-repo-commits))
    (GET "/branches"
         request
      (repo-viewer request view-repo-branches))
    (GET "/tags"
         request
      (repo-viewer request view-repo-tags)))
  (route/not-found "Not Found"))

;;暂时先去除csrf保护可以用调试工具调试post请求
(def app
  (-> app-routes
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (middleware/wrap-json-body)
      (middleware/wrap-json-params)))
