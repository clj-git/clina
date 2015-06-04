(ns clina.account-test
  (:require [clojure.test :refer :all]
            [bcrypt-clj.auth :refer :all]))

(deftest auth-password
  (testing "encrypt password with salt"
    (println (crypt-password "cleantha")))
  (testing "validate password"
    (is (true? (check-password "cleantha" "$2a$10$mxMtccSvksOj3BwC3fzvQul/whiXNihQXgE964Jqzj9Pb0OjtLPmm")))
    (is (false? (check-password "password" "$2a$10$mxMtccSvksOj3BwC3fzvQul/whiXNihQXgE964Jqzj9Pb0OjtLPmm")))))
