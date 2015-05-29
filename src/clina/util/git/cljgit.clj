(ns clina.util.git.cljgit
  (:require [clina.util.timeutil :refer :all])
  (:import (org.clina.core MockCore)))

(defn delete-repo
  [owner repository]
  (MockCore/deleteRepository owner repository))

(defn init-repo
  [owner repository]
  (MockCore/initBareRepo owner repository))

(defn list-commits
  [owner repository revision page path]
  (let [commits (into [] (java.util.ArrayList. (MockCore/viewRepoWithCommits owner repository revision page path)))]
    commits))

;;revision -> branch or tag name
(defn list-file
  ([request]
   (let [params (filter identity
                  (map
                    (fn [inner]
                      (get-in request [:params inner])) [:owner :repository :revision :*]))]
     (apply list-file params)))
  ([owner repository]
   (list-file owner repository "master" "."))
  ([owner repository revision]
   (list-file owner repository revision "."))
  ([owner repository revision path]
   (let [last-modified-commit (MockCore/getLastModifiedCommit owner repository revision path)
         parent-paths (into [] (java.util.ArrayList. (MockCore/getParentPaths path)))
         files (into [] (java.util.ArrayList. (MockCore/getRepoFiles owner repository revision path)))]
     {:last-modified-commit {:hash     (-> last-modified-commit (.getName))
                             :interval (get-time-interval (-> last-modified-commit (.getCommitTime)))
                             :author   (-> last-modified-commit (.getAuthorIdent) (.getName))}
      :parent-paths         parent-paths
      :files                (map (fn [file]
                                   {:name     (.-name file)
                                    :message  (.-message file)
                                    :author   (.-author file)
                                    :interval (get-time-interval
                                                (get-unix-timestamp (.time file)))}) files)
      :currentrevision      (if (nil? revision)
                              "master"
                              revision)})))