(ns hachiba.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :refer :all]
            [hiccup.form :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]))

;atoms
(def page (atom {:user "vaso"}))
(def page-names (atom {:pagename "page_id"}))
(def page-threads (atom {:pagename ["thread_ids"]})) ; ordered based on thread stats
(def page-terms (atom {:pagename ["terms"]}))
(def threads (atom {:thread_id ["post_ids"]}))
(def posts (atom {:post_id "post_id"
                   :contents "content"
                   :timestamp 1018
                   :num_posts 1
                   :last-touched 1018}))
(def latest-threads (atom {:fresh-threads ["thread_id"]}))


;fxns
(defn page-for-post
  "Given a post id, return the pagename"
  [post_id]
  (str "stetcher" post_id))
(defn thread-for-post [] )
(defn post-for-query [] )
(defn terms-for-page [] )


(defn submit-post [crown]
  (println "crown: " crown))



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

          :component/search (html [:div#searchbar.main
                                   [:div "practicalhuman.org/"
                                    [:input#searchbox]]])

          :component/body (html (let [coll [1 2 3 4 5 6 7]]
                            [:body
                            [:link {:type "text/css", :href "css/hachiba.css", :rel "stylesheet"}]
                            [:br][:br]
                            [:p
                            [:div {:class "HelloBox"} "Hello!"]
                            [:ul (for [x coll] [:li x])]]]))

          :component/footer (html [:div#footing "top folds"
                                   [:div#topfolds.main
                                    [:div "/nature"]
                                    [:div "/wigwam"]
                                    [:div "/jokes"]]])
          :component/input (let [crown (int (Math/floor (* 1000 (rand))))]
                             (html (form-to
                                     [:post "/post"]
                                     (hidden-field {:value crown} "capval")
                                     (text-area {:placeholder "post content"} "post_content")
                                     (text-field {:placeholder crown} "captcha")
                                     (submit-button {:class "btn"
                                                     :id "post_submit"
                                                     :onSubmit (submit-post crown)} "Post"))))
          })

(defroutes hachiba-routes
  (GET "/" [] (concat (:component/search cm)
                      (:component/header cm)
                      (:component/menu cm)
                      (:component/body cm)
                      (:component/input cm)
                      (:component/footer cm)))
  (GET "/hello" [] (str "Hello person! Welcome to Hachiba!"))
  (POST "/post" [params :as params] (str params))
  (route/not-found "Not Found"))

;(def app
;  (wrap-defaults app-routes api-defaults))

(def app
  (-> (wrap-defaults hachiba-routes api-defaults)
      (wrap-resource "public")))


