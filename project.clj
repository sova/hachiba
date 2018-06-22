(defproject com.nonforum/hachiba "0.0.0"
  :description "Hachiba: Online Posting Board"
  :url "https://nonforum.com/hachiba"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [funcool/bide "1.6.0"] ;faster routing than compojure
                 [hiccup "1.0.5"]
                 ]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler hachiba.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
