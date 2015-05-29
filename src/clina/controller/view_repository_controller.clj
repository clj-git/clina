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
        btinfo (with-branchs-tags request repoinfo)
        result (fn request btinfo)
        pagename (get-fn-name fn)]
    (if (zero? commitcount)
      (layout/render "emptyrepo")
      (layout/render pagename
                     (merge result repoinfomap)))))

(defn with-branchs-tags
  [request repoinfo]
  (let [branchs (apply with-repo-object (conj repoinfo get-repo-branches))
        tags (apply with-repo-object (conj repoinfo get-repo-tags))
        revision (get-in request [:params :revision])
        revs (if (or (contains? (set branchs) revision) (nil? revision))
               branchs
               (map :name tags))]
    {:branchs branchs
     :tags tags
     :revs revs}))

(defn view-repo
  [request btinfo]
  (let [result (list-file request)]
    (assoc result
      :revs (:revs btinfo)
      :tagcount (count (:tags btinfo)))))

(defn view-repo-commits
  [request btinfo]
  (let [commits (list-commits request)]
    {:commits commits
     :revs (:revs btinfo)}))

(defn view-repo-tags
  [request btinfo]
  {:tags (reverse (:tags btinfo))})