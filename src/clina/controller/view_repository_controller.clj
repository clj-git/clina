(ns clina.controller.view-repository-controller
  (:require [clina.util.git.cljgit :refer :all]
            [clina.util.git.gitcore :refer :all]
            [clina.view.layout :as layout]
            [clina.util.serviceutil :refer :all]))

(def git-repo-keys [:owner :repository])

(defn with-branches-tags
  [request repoinfo]
  (let [branches (apply with-repo-object (conj repoinfo get-repo-branches-withinfo))
        tags (reverse (apply with-repo-object (conj repoinfo get-repo-tags)))
        revision (get-in request [:params :revision])
        branchnames (map :name branches)
        tagnames (map :name tags)
        revs (if (or (contains? (set branchnames) revision) (nil? revision))
               branchnames
               tagnames)]
    {:branches branches
     :tags     tags
     :revs     revs
     :revision revision}))

(defn repo-viewer
  [request func]
  (let [repoinfomap (reduce
                      #(assoc %1 %2 (get-in request [:params %2])) {} git-repo-keys)
        repoinfo (vec (map #(get-in request [:params %]) git-repo-keys))
        commitcount (apply with-repo-object (conj repoinfo get-repo-commit-count))
        btinfo (merge repoinfomap (with-branches-tags request repoinfo))
        result (func request btinfo)
        pagename (get-fn-name func)]
    (if (zero? commitcount)
      (layout/render "emptyrepo")
      (layout/render pagename
                     (letfn [(get-default-revision [infomap]
                                                   (merge-with #(if (nil? %1) %2 %1) infomap {:revision "master"}))]
                       (assoc
                         (merge
                           (merge result
                                  (get-default-revision btinfo)))
                         :branchcount (count (:branches btinfo))
                         :tagcount (count (:tags btinfo))
                         :commitcount commitcount))))))

(defn view-repo
  [request info]
  (let [files (list-file request)]
    files))

(defn view-repo-commits
  [request info]
  (let [commits (list-commits request)]
    commits))

(defn view-repo-branches
  [request info])

(defn view-repo-tags
  [request info])

(defn view-repo-commit
  [request info]
  (let [params (vec (map #(% info) git-repo-keys))
        commithash (get-in request [:params :commithash])
        get-revisions (fn [callback] (apply with-repo-object (conj params callback commithash)))
        branches (get-revisions get-commit-branches)
        tags (get-revisions get-commit-tags)
        diffs (apply get-commit-diffs (conj params commithash))]
    (println diffs)
    {:commitbranches branches
     :committags     tags
     :commithash     commithash
     :diffs          diffs}))

(defn get-commit-diff-count
  [request]
  (let [params (map #(get-in request [:params %]) [:owner :repository :commithash])
        diffs (apply get-commit-diffs params)]
    (count diffs)))