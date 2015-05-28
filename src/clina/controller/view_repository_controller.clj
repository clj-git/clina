(ns clina.controller.view-repository-controller
  (:require [clina.util.git.cljgit :refer :all]
            [clina.util.git.gitcore :refer :all]
            [clina.view.layout :as layout]
            [clina.util.serviceutil :refer :all]))

(defn repo-viewer
  [request fn]
  (let [repoinfomap (reduce
                      #(assoc %1 %2 (get-in request [:params %2])) {} [:owner :repository])
        repoinfo (vec (map #(get-in request [:params %]) [:owner :repository]))
        commitcount (apply with-repo-object (conj repoinfo get-repo-commit-count))
        result (fn request repoinfo)
        pagename (get-fn-name fn)]
    (if (zero? commitcount)
      (layout/render "emptyrepo")
      (layout/render pagename
                     (merge result repoinfomap)))))

(defn view-repo
  [request repoinfo]
  (let [branchs (apply with-repo-object (conj repoinfo get-repo-branches))
        tags (apply with-repo-object (conj repoinfo get-repo-tags))
        revision (get-in request [:params :revision])
        result (list-file request)]
    (let [revs (if (or (contains? (set branchs) revision) (nil? revision))
                 branchs
                 (map :name tags))]
      (assoc result
        :revs revs
        :tagcount (count tags)))))

(defn view-repo-tags
  [request repoinfo]
  (let [tags (apply with-repo-object (conj repoinfo get-repo-tags))]
    {:tags (reverse tags)}))