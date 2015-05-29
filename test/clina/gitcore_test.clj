(ns clina.gitcore-test
  (:require [clojure.test :refer :all]
            [clina.util.git.cljgit :refer :all]))

(deftest init-bare-repo
  (testing "create bare repo"
    (let [owner (str "owner" (System/currentTimeMillis))
          repo (str "repo" (System/currentTimeMillis))
          result (init-repo owner repo)]
      (do (println "create a bare repo done")
          (delete-repo owner repo)
          (is (= result true))))))

(deftest repo-commits
  (testing "get repo commits"
    (let [commits (list-commits "root" "hehehe" "jihui_dev" 1 ".")]
      (doall
        (map #(println (str (.-id %) (.-authorTime %) (.-commitTime %))) commits)))))