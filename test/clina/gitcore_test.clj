(ns clina.gitcore-test
  (:require [clojure.test :refer :all]
            [clina.util.git.cljgit :refer :all]))

(deftest test-git
  (testing "create bare repo"
    (let [owner (str "owner" (System/currentTimeMillis))
          repo (str "repo" (System/currentTimeMillis))
          result (init-repo owner repo)]
      (do (delete-repo owner repo)
          (is (= result true))))))