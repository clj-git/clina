(defproject clina "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :source-paths ["src"]
  :java-source-paths ["java"]
  :prep-tasks ["javac" "compile"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [org.eclipse.jgit/org.eclipse.jgit.archive "3.4.1.201406201815-r"]
                 [org.eclipse.jgit/org.eclipse.jgit.http.server "3.4.1.201406201815-r"]
                 [com.googlecode.juniversalchardet/juniversalchardet "1.0.3"]
                 [commons-io/commons-io "2.4"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler clina.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
