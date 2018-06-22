(ns hachiba.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" []
       (let [coll [1 2 3 4 5 6 7]]
             (html [:p
                     [:span {:class "HelloBox"} "Hello!"]
                     [:ul (for [x coll] [:li x])]]))))
  (GET "/" []
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
