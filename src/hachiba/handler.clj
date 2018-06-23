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



(defn add-to-index [post thread_id]
  )

(defn new-thread [boardname content]
  ;generate a post id
  ;move into atom for @posts
  ;generate a thread id
  ;save the post id associated with the thread id to the @threads atom
  ;save the thread id to the @page-threads atom
  )





(defn submit-post [crown]
  (println "crown: " crown))

(defn submit-boardnav []
  (clojure.string/replace "A(B%$c32d" #"[^a-zA-Z]" "")
  (println "boardnav submit event"))

;        蜂場
;      hachiba
;place/field of bees

;       実用人
;     jitsuyou hito
;practical person

(def cm { :component/menu (html [:div#menu.main
                                 [:div.menulinks
                                 [:div#hachiba_title "蜂場"]
                                 [:div.link [:a {:href "/"} "top page"]]
                                 ;[:div (str "logged in: " (:user @page))]
                                 ;[:div.link [:a {:href "/user"} "points"]]
                                 [:div.link [:a {:href "/about"} "about"]]

                                 [:div.link [:a {:href "/fresh"} "fresh"]]
                                 [:div.link [:a {:href "/submit"} "submit"]]
                                 ;[:div.link [:a {:href "/favorites"} "favorites"]]
                                 ;[:div.link [:a {:href "/logout"} "logout"]]
                                 [:div.link [:a {:href "/index"} "complete index"]]]])

          :component/search (html [:div#searchbar.main
                                    [:span [:a {:href "/"} "practicalhuman.org/"]
                                     (form-to
                                       [:post "/boardnav"]

                                       (text-field {:placeholder "enter a boardname or create one" :maxlength 37 :id "searchbox"} "::")
                                       (submit-button {:class "btn"
                                                      :id "boardnav_submit"
                                                      :onSubmit (submit-boardnav)} "go"))]])

          :component/body (html (let [coll [1 2 3 4 5 6 7]]
                            [:body
                             [:div#main_contain
                              [:link {:type "text/css", :href "/css/hachiba.css", :rel "stylesheet"}]
                              [:br][:br]
                              [:p
                              [:div {:class "latest"} "Latest Posts"]
                              [:ul (for [x coll] [:li x])]]]]))

          :component/footer (html [:div#footing "top folds"
                                   [:div#topfolds.main
                                    [:a.fold {:href "/nature"} "/nature"]
                                    [:a.fold {:href "/wigwam"} "/wigwam"]
                                    [:a.fold {:href "/jokes"}  "/jokes"]]])
          })


(defn comment-input [term]
  (let [crown (int (Math/floor (* 1000 (rand))))]
                             (html (form-to
                                     [:post "/post"]
                                     (hidden-field {:value crown} "capval")
                                     (hidden-field {:value term} "boardname")
                                     (text-area {:placeholder "post content"} "post_content")
                                     (text-field {:placeholder crown} "captcha")
                                     (submit-button {:class "btn"
                                                     :id "post_submit"
                                                     :onSubmit (submit-post crown)} "Post")))))

(defn html-footer
  "Generates a footer element given a collection of folds (board names)"
  [coll]
  (html [:div#footing "top folds"
         [:div#topfolds.main
         (for [fold coll]
           [:a.fold {:href (str "/" fold)} (str "/" fold)])]]))


(defn draw-threads [])
(defn get-threads [term]
  ;return a vector of threads that live on this page
  )








(def folds ["nature" "pomp" "waves" "wigwam"])

(defroutes hachiba-routes
  (GET "/" [] (concat (:component/search cm)
                      (:component/header cm)
                      (:component/menu cm)
                      (:component/body cm)
                      (html-footer folds)))

  (POST "/boardnav" [params :as params]
    (let [desired-url (:form-params params)
          sanitized (clojure.string/replace desired-url #"[^a-zA-Z0-9\s]" "")]

      ;(println sanitized)
      {:status 302
       :headers {"Location" sanitized}
       :body ""}))

  (GET "/user" [params :as params] (str "user!" params))
  (GET "/about" [params :as params] (str "about!" params))
  (GET "/fresh" [params :as params] (str "fresh!" params))
  (GET "/submit" [params :as params] (str "submit!" params))
  (GET "/favorites" [params :as params] (str "favorites!" params))
  (GET "/logout" [params :as params] (str "logout!" params))
  (GET "/index" [params :as params] (str "complete index!" params))

  (GET "/:term" [term]
              (concat (:component/search cm)
                      (html [:div#topterm (str "now browsing /"   term)])
                      (:component/header cm)
                      (:component/menu cm)
                      (draw-threads (get-threads term))
                      (comment-input term)
                      ;(html-footer folds)
                      ))


  (GET "/:term/" [term]
              (concat (:component/search cm)
                      (html [:div#topterm (str "now browsing /" term "/")])
                      (:component/header cm)
                      (:component/menu cm)
                      (draw-threads (get-threads term))
                      (comment-input term)
                      ;(html-footer folds)
                      ))



  (POST "/post" [params :as params]
    (let [fp (:form-params params)
          capval (get fp "capval")
          boardname (get fp "boardname")
          content (get fp "post_content")
          captcha (get fp "captcha")
          sanitized (clojure.string/replace content #"[^a-zA-Z0-9\s.()]" "")]

      (if (= capval captcha)
        (do
          (println "/" boardname)
          (println content)
          (new-thread boardname content)))))

  (route/not-found "Not Found"))


(def app
  (-> (wrap-defaults hachiba-routes api-defaults)
      (wrap-resource "public")))


