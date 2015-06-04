(ns clina.base-test
  (:require [clojure.test :refer :all]
            [environ.core :refer [env]]))

(deftest get-basepath
  (testing "get basepath from env variable"
    (println (env :clina-data))))
