(ns clina.gitcore
  (:require [clina.models :refer :all])
  (:import (org.eclipse.jgit.api Git LogCommand)
           (org.eclipse.jgit.lib RepositoryBuilder Repository ObjectId Ref PersonIdent)
           (org.eclipse.jgit.api.errors NoHeadException)
           (org.eclipse.jgit.revwalk RevWalk RevTag RevCommit)
           (org.clina.core MockCore))
  (:use [clojure.string :only (join)]))

(def basepath MockCore/basepath)

(defn get-repo-dir
  [owner repository]
  (let [repo-home (str basepath "/repositories")
        repo-dir (str (join "/" [repo-home owner repository]) ".git")]
    (let [bare-repo (-> repo-dir
                        (java.io.File.))]
      bare-repo)))

(defn get-repo-obj
  ^Git [owner repository]
  (let [repo-dir (get-repo-dir owner repository)]
    (Git/open repo-dir)))

(defn with-repo-object
  [owner repository repo-operation]
  (let [git (get-repo-obj owner repository)]
    (repo-operation git)))

;;在项目主页上显示提交次数
(defn get-repo-commit-count
  [^Git git]
  (try
    (let [commitlist (iterator-seq (-> git (.log) (.all) (.call) (.iterator)))]
      (count commitlist))
    (catch NoHeadException e 0)))

;;在项目主页上可以用select选择不同的branch
(defn get-repo-branches
  [^Git git]
  (let [branchlist (-> git (.branchList) (.call))]
    (map
      (fn [branch]
        (-> branch (.getName) (.replace "refs/heads/" ""))) branchlist)))

;;id就是commithash
(defn get-revcommit-from-id
  [^Git git ^ObjectId objectid]
  (let [revwalk (-> git (.getRepository) (RevWalk.))
        object (.parseAny revwalk objectid)]
    (let [commit (if (= (type object) org.eclipse.jgit.revwalk.RevTag)
                   (.parseCommit revwalk (.getObject (cast RevTag object)))
                   (.parseCommit revwalk objectid))]
      (.dispose revwalk)
      commit)))

;;在项目主页上可以用select选择不同的tag
;;tag的时间就是这个tag指向的最后一次提交的时间
(defn get-repo-tags
  [^Git git]
  (let [taglist (-> git (.tagList) (.call))]
    (map
      (fn [ref]
        (let [revcommit (get-revcommit-from-id git (.getObjectId ref))]
          (->TagInfo (-> ref (.getName) (.replace "refs/tags/" "")) (-> revcommit (.getCommitterIdent) (.getWhen)) (.getName revcommit)))) taglist)))
