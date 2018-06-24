(defproject com.nonforum/hachiba "0.0.0"
  :description "Hachiba: Online Posting Board"
  :url "https://nonforum.com/hachiba"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [trail "2.1.0"]
                 [ring/ring-defaults "0.2.1"]
                 [hiccup "1.0.5"]
                 [clj-time "0.14.4"]
                 ]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler hachiba.handler/app
         :port 1337}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
