(ns clina.models)

(defrecord RepositoryInfo
  [owner name commitcount branchlist taglist])