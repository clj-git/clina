(ns clina.util.git.cljgit
  (:require [clina.util.timeutil :refer :all]
            [clina.util.serviceutil :refer :all])
  (:import (org.clina.core MockCore)))

(defn delete-repo
  [owner repository]
  (MockCore/deleteRepository owner repository))

(defn init-repo
  [owner repository]
  (MockCore/initBareRepo owner repository))

(defn list-commits
  ([request]
   (let [keys [:owner :repository :revision :page :*]
         params
         (reduce
           (fn [params key]
             (assoc params key (get-in request [:params key]))) {} keys)
         params-withdefault (merge-with #(if (nil? %1) %2 %1) params {:revision "master" :page "1" :* "."})]
     (apply list-commits (map (fn [key] (key params-withdefault)) keys))))
  ([owner repository revision page path]
   (let [intpage (Integer/parseInt page)
         commits (arraylist2vector (MockCore/viewRepoWithCommits owner repository revision intpage (if (= path ".") path (str path "/"))))]
     {:commits (reduce
                 (fn [commitlist commit]
                   (conj commitlist {:commitmsg  (.-summary commit)
                                     :author     (.-authorName commit)
                                     :commithash (.-id commit)
                                     :interval   (get-time-interval (get-unix-timestamp (.-commitTime commit)))})) [] commits)
      :prev (dec intpage)
      :next (inc intpage)})))

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
         parent-paths (arraylist2vector (MockCore/getParentPaths path))
         files (arraylist2vector (MockCore/getRepoFiles owner repository revision path))]
     {:last-modified-commit {:hash     (-> last-modified-commit (.getName))
                             :interval (get-time-interval (-> last-modified-commit (.getCommitTime)))
                             :author   (-> last-modified-commit (.getAuthorIdent) (.getName))}
      :parent-paths         parent-paths
      :files                (map (fn [file]
                                   {:name     (.-name file)
                                    :message  (.-message file)
                                    :author   (.-author file)
                                    :interval (get-time-interval
                                                (get-unix-timestamp (.time file)))}) files)})))