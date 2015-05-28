(ns clina.controller.view-repository-controller
  (:require [clina.cljgit :refer :all]
            [clina.gitcore :refer :all]
            [clina.view.layout :as layout]))

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

(defn view-repo-tags
  [request]
  (let [repoinfo (vec (map #(get-in request [:params %]) [:owner :repository]))
        tags (apply with-repo-object (conj repoinfo get-repo-tags))]
    (layout/render "reporelease" {:tags         (reverse tags)
                                  :currentowner (get-in request [:params :owner])
                                  :currentrepo  (get-in request [:params :repository])})))