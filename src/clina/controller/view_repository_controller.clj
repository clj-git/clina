(ns clina.controller.view-repository-controller
  (:require [clina.util.git.cljgit :refer :all]
            [clina.util.git.gitcore :refer :all]
            [clina.view.layout :as layout]
            [clina.util.serviceutil :refer :all]))

(defn with-branchs-tags
  [request repoinfo]
  (let [branchs (apply with-repo-object (conj repoinfo get-repo-branches-withinfo))
        tags (reverse (apply with-repo-object (conj repoinfo get-repo-tags)))
        revision (get-in request [:params :revision])
        branchnames (map :name branchs)
        tagnames (map :name tags)
        revs (if (or (contains? (set branchnames) revision) (nil? revision))
               branchnames
               tagnames)]
    {:branchs  branchs
     :tags     tags
     :revs     revs
     :revision revision}))

(defn repo-viewer
  [request fn]
  (let [repoinfomap (reduce
                      #(assoc %1 %2 (get-in request [:params %2])) {} [:owner :repository :revision])
        repoinfo (vec (map #(get-in request [:params %]) [:owner :repository]))
        commitcount (apply with-repo-object (conj repoinfo get-repo-commit-count))
        btinfo (with-branchs-tags request repoinfo)
        result (fn request btinfo)
        pagename (get-fn-name fn)]
    (if (zero? commitcount)
      (layout/render "emptyrepo")
      (layout/render pagename
                     (letfn [(get-default-revision [infomap]
                                                   (merge-with #(if (nil? %1) %2 %1) infomap {:revision "master"}))]
                       (assoc
                         (merge
                           (merge result
                                  (get-default-revision repoinfomap))
                           (get-default-revision btinfo))
                         :branchcount (count (:branchs btinfo))
                         :tagcount (count (:tags btinfo))
                         :commitcount commitcount))))))

(defn view-repo
  [request btinfo]
  (let [result (list-file request)]
    result))

(defn view-repo-commits
  [request info]
  (let [commits (list-commits request)]
    {:commits commits}))

(defn view-repo-branchs
  [request info])

(defn view-repo-tags
  [request info])