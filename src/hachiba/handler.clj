(ns hachiba.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :refer :all]
            [hiccup.form :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [clj-time.core :as t]))

;atoms
;(def page (atom {:user "vaso"}))
;(def page-names (atom {:pagename "page_id"}))
(def page-threads (atom [{:pagename "hax"
                          :threads [10029]}
                         {:pagename "top"
                          :threads [10031 10033]}])) ; ordered based on thread stats
;(def page-terms (atom {:pagename "pagename"
;                       :terms ["terms"]}))
(def threads (atom [{:thread_id "10029"
                     :posts [17778 17780]}
                    {:thread_id 10031
                     :posts [17779]}
                    {:thread_id 10033
                     :posts [18881 18882 18883]}]))

(def posts (atom [{:post_id 17778
                   :contents "I'll start this thread!"
                   :timestamp 1018
                   :thread_id 10029}
                  {:post_id 17779
                   :contents "Well, this is a neat experiment..."
                   :timestamp 1018
                   :thread_id 10029}
                  {:post_id 17780
                   :contents "Let's try it out!"
                   :timestamp 1018
                   :thread_id 10029}
                  {:post_id 18881
                   :contents "Magic!"
                   :timestamp 1022
                   :thread_id 10029}
                  {:post_id 18882
                   :contents "You best believe it."
                   :timestamp 1028
                   :thread_id 10029}
                  {:post_id 18883
                   :contents "Harpoons and Cartoons!"
                   :timestamp 1033
                   :thread_id 10029}]))

(def latest-threads (atom {:fresh-threads ["thread_id"]}))


;fxns
(defn find-index [term]
  (reduce-kv (fn [_ idx m]
     (if (= term (:pagename m))
            (reduced idx)))
          nil
          @page-threads))

;(find-index "top")
;(find-index "hax")

(defn update-page-threads [pagename new-thread-id]
  (let [idx (find-index pagename)
        page-thread-map (get @page-threads idx)]
    (assoc @page-threads idx {:pagename pagename
                              :threads (vec (distinct (conj (:threads page-thread-map) new-thread-id)))})))



 (defn find-index-tid [tid]
  (reduce-kv (fn [_ idx m]
     (if (= tid (:thread_id m))
            (reduced idx)))
          nil
          @threads))

(defn update-threads [tid pid]
  (let [idx (find-index-tid tid)]
    (if (= nil idx)
      (conj @threads {:thread_id tid
                      :posts [pid]})
      ;else
      (let [thread-map (get @threads idx)]
        (assoc @threads idx {:thread_id tid
                             :posts (vec (distinct (conj (:posts thread-map) pid)))})))))






(def vints (atom 12345678))
;(defn uuid [] (subs (.toString (java.util.UUID/randomUUID)) 0 8))
(defn uuid []
  (swap! vints inc)
  @vints)


(defn page-for-post
  "Given a post id, return the pagename"
  [post_id]
  (str "stetcher" post_id))
(defn thread-for-post [] )
(defn post-for-query [] )
(defn terms-for-page [] )

(defn get-posts-by-thread [tid]
  (let [posts-map (filter (fn [{:keys [thread_id]}] (= tid thread_id)) @threads)
        posts (:posts (first posts-map))]

    posts))



(defn get-thread-by-post [pid]
  (let [thread-map (filter (fn [{:keys [post_id]}] (= post_id pid)) @posts)
        thread (:thread_id (first thread-map))]
    thread))

(defn get-threads-by-page [term]
  (let [thread-map (filter (fn [{:keys [pagename]}] (= pagename term)) @page-threads)
        threads (:threads (first thread-map))]
    threads))

(defn get-post-by-id [pid]
  (let [post-map (filter (fn [{:keys [post_id]}] (= post_id pid)) @posts)]
    post-map))



(println "# " (get-posts-by-thread "10029"))

(println "## " (get-thread-by-post 17779))

(println "### " (get-threads-by-page "top"))


(defn new-post
  "1. Generate new ID for post (and if not set, thread).
   2. Update posts-atom, threads-atom, page-terms-atom"
  [boardname thread-id content]
  (if (nil? thread-id)
    (let [thread-id (uuid)
          post-id (uuid)]
      ;add to posts
      (swap! posts conj {:thread-id (str thread-id)
                         :post-id (str post-id)
                         :content content
                         :timestamp (quot (System/currentTimeMillis) 1000)})

      ;update threads
      (reset! threads (update-threads thread-id post-id))


      ;update page-threads
      (reset! page-threads (update-page-threads boardname thread-id))


      (println @posts)
      (println @threads)
      (println @page-threads))
    ;else, not nil thread-id
    (let [post-id (uuid)]
      (do
        ;add to posts
        (swap! posts conj {:thread-id thread-id
                           :post-id (str post-id)
                           :content content
                           :timestamp (quot (System/currentTimeMillis) 1000)})
        ;update threads
        (reset! threads (update-threads thread-id post-id))

        ;update page-threads
        (reset! page-threads (update-page-threads boardname thread-id))))))





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

(def cm { :component/menu (html [:link {:type "text/css", :href "/css/hachiba.css", :rel "stylesheet"}]
                                [:div#menu.main
                                 [:div.menulinks
                                 [:div#hachiba_title "蜂場"]
                                 [:div.link [:a {:href "/" :class "sblink"} "top page"]]
                                 ;[:div (str "logged in: " (:user @page))]
                                 ;[:div.link [:a {:href "/user" :class "sblink"} "points"]]
                                 [:div.link [:a {:href "/about" :class "sblink"} "about"]]

                                 [:div.link [:a {:href "/fresh" :class "sblink"} "fresh"]]
                                 [:div.link [:a {:href "/submit" :class "sblink"} "submit"]]
                                 ;[:div.link [:a {:href "/favorites" :class "sblink"} "favorites"]]
                                 ;[:div.link [:a {:href "/logout" :class "sblink"} "logout"]]
                                 [:div.link [:a {:href "/index" :class "sblink"} "complete index"]]]])

          :component/search (html [:div#searchbar.main
                                    [:span [:a {:href "/" :id "sblink"} "practicalhuman.org/"]
                                     (form-to
                                       [:post "/"] ;used to be boardnav but f that

                                       (text-field {:placeholder "enter a boardname or create one" :maxlength 37 :id "searchbox" :autofocus true} "::")
                                       (submit-button {:class "btn"
                                                      :id "boardnav_submit"
                                                      :onSubmit (submit-boardnav)} "go"))]])

          :component/body (html (let [coll [1 2 3 4 5 6 7]]
                            [:body
                             [:div#main_contain
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
                                     (text-area {:placeholder (str "post to " term)} "post_content")
                                     (text-field {:placeholder crown} "captcha")
                                     (submit-button {:class "btn"
                                                     :id "post_submit"
                                                     :onSubmit (submit-post crown)} "Post")))))

(defn comment-input-with-tid [term tid]
  (let [crown (int (Math/floor (* 1000 (rand))))]
                             (html (form-to
                                     [:post "/post"]
                                     (hidden-field {:value crown} "capval")
                                     (hidden-field {:value term} "boardname")
                                     (hidden-field {:value tid} "thread_id")
                                     (text-area {:placeholder (str "post to " term)} "post_content")
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


(defn draw-threads-for-page
  "Returns HTML rendering of threads coll"
  [term]

  (html
    [:div.threads
    (let [threads (get-threads-by-page term)]
       (for [thread threads]
         [:div.thread {:thread_id thread}
          [:a {:href (str "/" term "/" thread)}
           (let [post-ids (get-posts-by-thread thread)]
             (for [pid post-ids]
               (let [post-map (first (get-post-by-id pid))]
                 [:div.post
                   [:div.post_content (:contents post-map)]
                   [:div.post_timestamp "posted at " (:timestamp post-map)]])))]]))]))




(defn draw-posts-for-thread
  "Returns HTML rendering of all the posts in a given thread"
  [tid]
  (html
    [:div.thread {:id tid}
     (println "get posts by thread tid" (get-posts-by-thread tid))
         (let [post-ids (get-posts-by-thread tid)]
           (for [pid post-ids]
             (let [post-map (first (get-post-by-id pid))]
               [:div.post
                 [:div.post_content (:contents post-map)]
                 [:div.post_timestamp "posted at " (:timestamp post-map)]])))]))



(def folds ["nature" "pomp" "waves" "wigwam"])

(defroutes hachiba-routes
  (GET "/" [] (concat (:component/search cm)
                      (:component/header cm)
                      (:component/menu cm)
                      (:component/body cm)
                      (html-footer folds)))

  (POST "/" [params :as params]
    (let [desired-url (:form-params params)
          sanitized (clojure.string/replace desired-url #"[^a-zA-Z0-9\s]" "")]
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
                      (draw-threads-for-page term)
                      (comment-input term)
                      (uuid)
                      ;(html-footer folds)
                      ))


  (GET "/:term/" [term]
              (concat (:component/search cm)
                      (html [:div#topterm (str "now browsing /" term "/")])
                      (:component/header cm)
                      (:component/menu cm)
                      (draw-threads-for-page term)
                      (comment-input term)
                      ;(html-footer folds)
                      ))

  (GET "/:term/:tid" [term tid]
              (concat (:component/search cm)
                      (html [:div#topterm "now browsing /" [:a {:href (str "/" term)} term] (str "/" tid)])
                      (:component/header cm)
                      (:component/menu cm)
                      (println "gpbt" (get-posts-by-thread tid))
                      (draw-posts-for-thread tid)
                      (println "tid" tid)
                      (comment-input-with-tid term tid)
                      ;(html-footer folds)
                      ))




  (POST "/post" [params :as params]
    (let [fp (:form-params params)
          capval (get fp "capval")
          boardname (get fp "boardname")
          content (get fp "post_content")
          captcha (get fp "captcha")
          sanitized (clojure.string/replace content #"[^a-zA-Z0-9\s.()]" "")
          thread-id (get fp "thread_id")
          timestamp (quot (System/currentTimeMillis) 1000)]

      (if (= capval captcha)
        (do
          (println "/" boardname)
          (println thread-id)
          (println content)
         ; (println thread-id " : is the thread-id")
          (new-post boardname thread-id content)
          {:status 302
           :headers {"Location" boardname}
           :body ""}))))

  (route/not-found "Not Found"))


(def app
  (-> (wrap-defaults hachiba-routes api-defaults)
      (wrap-resource "public")))


