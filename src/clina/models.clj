(ns clina.models)

(defrecord RepositoryInfo
  [owner name commitcount branchlist taglist])

(defrecord TagInfo [name date id])
