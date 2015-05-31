(ns clina.util.git.gitcore
  (:require [clina.models :refer :all]
            [clina.util.timeutil :refer :all]
            [clina.util.serviceutil :refer :all])
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
  [owner repository repo-operation & params]
  (let [git (get-repo-obj owner repository)]
    (apply repo-operation (cons git params))))

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

(defn get-repo-branches-withinfo
  [^Git git]
  (map
    (fn [branchname]
      (let [repobranch (-> git (.getRepository) (.resolve branchname))
            revcommit (-> git (.log) (.add repobranch) (.setMaxCount 1) (.call) (.iterator) (.next))
            revcommitdate (-> revcommit (.getCommitterIdent) (.getWhen))
            authorname (-> revcommit (.getCommitterIdent) (.getName))
            interval (get-time-interval (get-unix-timestamp revcommitdate))]
        {:name       branchname
         :interval   interval
         :authorname authorname})) (get-repo-branches git)))

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

;;e.getValue.getName.substring(org.eclipse.jgit.lib.Constants.R_HEADS.length)


;;branches tags
(defn get-commit-revisions
  [^Git git revision commitId]
  (let [revwalk (-> git (.getRepository) (RevWalk.))
        commit (.parseCommit revwalk (-> git (.getRepository) (.resolve (str commitId "^0"))))]
    (map
      (fn [obj]
        (let [value (nth obj 0)]
          (-> value (.substring (.length revision)))))
      (filter
        (fn [obj]
          (let [key (nth obj 0)
                value (nth obj 1)]
            (and (.startsWith key revision)
                 (-> revwalk (.isMergedInto commit
                                            (-> revwalk (.parseCommit (-> value (.getObjectId)))))))))
        (map2map (-> git (.getRepository) (.getAllRefs)))))))

(comment
  (filter
    (fn [entry])
    (-> git (.getRepository) (.getAllRefs) (.entry))))

(defn get-commit-branches
  "get branches of specific commit"
  [^Git git commitId]
  (get-commit-revisions git org.eclipse.jgit.lib.Constants/R_HEADS commitId))

(defn getTagsOfCommit
  "get tags of specific commit"
  [])
