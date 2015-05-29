(ns clina.gitcore-test
  (:require [clojure.test :refer :all]
            [clina.util.git.cljgit :refer :all]
            [clina.util.git.gitcore :refer :all]))

(deftest init-bare-repo
  (testing "create bare repo"
    (let [owner (str "owner" (System/currentTimeMillis))
          repo (str "repo" (System/currentTimeMillis))
          result (init-repo owner repo)]
      (do (println "create a bare repo done")
          (delete-repo owner repo)
          (is (= result true))))))

(deftest repo-commits
  (testing "get repo commits with"
    (let [commits (list-commits "root" "hehehe" "jihui_dev" "3" ".")]
      (doall
        (map println commits))))
  (testing "get repo commits with path"
    (let [commits (list-commits "root" "hehehe" "jihui_dev" "1" "hehe")]
      (doall
        (map println commits)))))

(deftest repo-branches
  (testing "get repo branches with info"
    (let [branches (apply with-repo-object (conj ["root" "hehehe"] get-repo-branches-withinfo))]
      (println branches))))