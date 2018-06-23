(ns hachiba.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def page (atom {:user "vaso"}))
(def cm { :component/header (html [:div#heading.top
                                   [:span "hachiba 蜂場 はちば"]
                                   [:div#menu.top "Hachiba"]])
          :component/menu (html [:div#menu.main
                                 [:div.hidden
                                 [:div (str "logged in as: " (:user @page))]
                                 [:div "points"]
                                 [:div "top page"]
                                 [:div "fresh"]
                                 [:div "submit"]
                                 [:div "favorites"]
                                 [:div "logout"]
                                 [:div "complete index"]]])

          :component/body (html (let [coll [1 2 3 4 5 6 7]]
                            [:body
                            [:link {:type "text/css", :href "css/hachiba.css", :rel "stylesheet"}]
                            [:p
                            [:span {:class "HelloBox"} "Hello!"]
                            [:ul (for [x coll] [:li x])]]]))})

(defroutes app-routes
  (GET "/" [] (concat (:component/header cm)
                      (:component/menu cm)
                      (:component/body cm)))
  (GET "/hello" [] (str "Hello person! Welcome to Hachiba!"))
  (GET "/:x" [x] (str x))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))



