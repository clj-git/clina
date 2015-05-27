(ns clina.cljgit
  (:import (org.clina.core MockCore)))

(defn delete-repo
  [owner repository]
  (MockCore/deleteRepository owner repository))

(defn init-repo
  [owner repository]
  (MockCore/initBareRepo owner repository))

