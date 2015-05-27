(ns clina.cljgit
  (:import (org.clina.core MockCore)))

(defn init-repo
  [owner repository]
  (MockCore/initBareRepo owner repository))

(init-repo "cleantha" "hehehe")