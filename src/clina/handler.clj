(ns clina.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as cheshire]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response content-type]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clina.cljgit :refer :all]
            [clina.gitcore :refer :all]
            [clina.view.layout :as layout]))

(defn json [form]
  (-> form
      cheshire/encode
      response
      (content-type "application/json; charset=utf-8")))

(defn view-repo
  [request]
  (let [repoinfo (vec (map #(get-in request [:params %]) [:owner :repository]))
        commitcount (apply with-repo-object (conj repoinfo get-repo-commit-count))]
    (if (zero? commitcount)
      (layout/render "emptyrepo")
      (let [branchs (apply with-repo-object (conj repoinfo get-repo-branches))
            tags (apply with-repo-object (conj repoinfo get-repo-tags))
            revision (get-in request [:params :revision])
            result (list-file request)]
        (let [revs (if (or (contains? (set branchs) revision) (nil? revision))
                     branchs
                     (map :name tags))]
          (layout/render "viewrepo" (assoc result
                                      :revs revs
                                      :tagcount (count tags))))))))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/new" []
    (layout/render "newrepo" {:title "just have fun"}))
  (POST "/new" request
    (let [owner (get-in request [:params :owner])
          repository (get-in request [:params :repository])]
      (json (init-repo owner repository))))
  (GET "/:owner/:repository"
       request
    (view-repo request))
  (GET "/:owner/:repository/tree/:revision"
       request
    (view-repo request))
  (GET "/:owner/:repository/tree/:revision/*"
       request
    (view-repo request))
  (route/not-found "Not Found"))

;;暂时先去除csrf保护可以用调试工具调试post请求
(def app
  (-> app-routes
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (middleware/wrap-json-body)
      (middleware/wrap-json-params)))
