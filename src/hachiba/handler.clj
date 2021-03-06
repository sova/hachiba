(ns hachiba.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.core :refer :all]
            [hiccup.form :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware [multipart-params :as mp]]
            [clojure.java.io :as io :refer [copy file]]
            [clj-time.core :as t]
            [duratom.core :refer :all]))

;atoms
;;(def page (atom {:user "vaso"}))
;;(def page-names (atom {:pagename "page_id"}))
;; ordered based on thread stats
;;(def page-terms (atom {:pagename "pagename"
;;                       :terms ["terms"]}))

;(def latest-threads (atom {:fresh-threads ["thread-id"]}))


(def page-threads (duratom :local-file
                           :file-path "page-threads.sova"
                           :init []))
(def threads (duratom :local-file
                      :file-path "threads.sova"
                      :init []))
(def posts (duratom :local-file
                    :file-path "posts.sova"
                    :init []))

(def last-modified (duratom :local-file
                            :file-path "last-modified.sova"
                            :init []))
(def most-threads (duratom :local-file
                           :file-path "most-threads.sova"
                           :init []))
(def most-posts (duratom :local-file
                         :file-path "most-posts.sova"
                         :init []))



(def gtag "<head><!-- Global site tag (gtag.js) - Google Analytics -->
<script async src='https://www.googletagmanager.com/gtag/js?id=UA-119547458-2'></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());

  gtag('config', 'UA-119547458-2');
</script></head>")



;fxns

;lives lower
;(defn upload-file [source-path]
; (io/copy (io/file source-path) (io/file (str "/uploads/pics/" (quot (System/currentTimeMillis) 1000) (rand-int 4) ".jpg"))))


(defn find-index [term]
  (reduce-kv (fn [_ idx m]
     (if (= term (:pagename m))
            (reduced idx)))
          nil
          @page-threads))

;(find-index "top")
;(find-index "hax")

(defn update-page-threads [pagename new-thread-id]
  (let [idx (find-index pagename)]
    (if (= nil idx)
      (conj @page-threads {:pagename pagename
                           :threads [new-thread-id]})
      ;else
      (let [page-thread-map (get @page-threads idx)]
    (assoc @page-threads idx {:pagename pagename
                              :threads (vec (distinct (conj (:threads page-thread-map) new-thread-id)))})))))



 (defn find-index-tid [tid]
  (reduce-kv (fn [_ idx m]
     (if (= tid (:thread-id m))
            (reduced idx)))
          nil
          @threads))

(defn update-threads [tid pid]
  (let [idx (find-index-tid tid)]
    (if (= nil idx)
      (conj @threads {:thread-id tid
                      :posts [pid]})
      ;else
      (let [thread-map (get @threads idx)]
        (assoc @threads idx {:thread-id tid
                             :posts (vec (distinct (conj (:posts thread-map) pid)))})))))






;(def vints (atom 12345678))
(defn uuid [] (subs (clojure.string/replace (.toString (java.util.UUID/randomUUID)) #"[^0-57-9A-Za-z]" "") 0 9))
;(defn uuid []
;  (int (swap! vints inc)))



(defn page-for-post
  "Given a post id, return the pagename"
  [post-id]
  (str "stetcher" post-id))
(defn thread-for-post [] )
(defn post-for-query [] )
(defn terms-for-page [] )

(defn get-posts-by-thread [tid]
  (let [posts-map (filter (fn [{:keys [thread-id]}] (= tid thread-id)) @threads)
        posts (:posts (first posts-map))]

    posts))



(defn get-thread-by-post [pid]
  (let [thread-map (filter (fn [{:keys [post-id]}] (= post-id pid)) @posts)
        thread (:thread-id (first thread-map))]
    thread))

(defn get-threads-by-page [term]
  (let [thread-map (filter (fn [{:keys [pagename]}] (= pagename term)) @page-threads)
        threads (:threads (first thread-map))]
    threads))

(defn get-post-by-id [pid]
  (let [post-map (filter (fn [{:keys [post-id]}] (= post-id pid)) @posts)]
    post-map))



;(println "# " (get-posts-by-thread "10029"))

;(println "## " (get-thread-by-post 17779))

;(println "### " (get-threads-by-page "top"))


(defn new-post
  "1. Generate new ID for post (and if not set, thread).
   2. Update posts-atom, threads-atom, page-terms-atom.
   3. Return thread-id."
  [boardname thread-id content image-url]
  ;(println "@ " thread-id)
  ;(println "@@ " image-url)
  (if (nil? thread-id)
    (let [thread-id (uuid)
          post-id (uuid)]
      ;add to posts
      (swap! posts conj {:thread-id (str thread-id)
                         :post-id (str post-id)
                         :content content
                         :image-url image-url ;can be nil
                         :timestamp (quot (System/currentTimeMillis) 1000)})

      ;update threads
      (reset! threads (update-threads thread-id post-id))


      ;update page-threads
      (reset! page-threads (update-page-threads boardname thread-id))


      ;(println @posts)
      ;(println @threads)
      ;(println @page-threads)
      ;(println @last-modified)
      (swap! last-modified conj boardname)
      thread-id)
    ;else, not nil thread-id
    (let [post-id (uuid)]
      (do
        ;add to posts
        (swap! posts conj {:thread-id thread-id
                           :post-id (str post-id)
                           :content content
                           :image-url image-url
                           :timestamp (quot (System/currentTimeMillis) 1000)})
        ;update threads
        (reset! threads (update-threads thread-id post-id))

        ;update page-threads
        (reset! page-threads (update-page-threads boardname thread-id))

        (swap! last-modified conj boardname)
        thread-id))))



(defn submit-post [crown]
  ;(println "crown: " crown)
  )

(defn submit-boardnav []
  (clojure.string/replace "A(B%$c32d" #"[^a-zA-Z]" "")
  ;(println "boardnav submit event")
  )

;        蜂場
;      hachiba
;place/field of bees

;       実用na人
;     jitsuyou na hito
;practical person

(def cm { :component/menu (html [:link {:type "text/css", :href "/css/hachiba.css", :rel "stylesheet"}]
                                [:div#menu.main
                                 [:div.menulinks
                                 [:div#hachiba_title "蜂場"]
                                 [:div.link [:a {:href "/" :class "sblink"} "top page"]]
                                 ;[:div (str "logged in: " (:user @page))]
                                 ;[:div.link [:a {:href "/user" :class "sblink"} "points"]]
                                 [:div.link [:a {:href "/about" :class "sblink"} "about"]]

                                 ;[:div.link [:a {:href "/fresh" :class "sblink"} "fresh"]]
                                 ;[:div.link [:a {:href "/submit" :class "sblink"} "submit"]]
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

          :component/latest (html
                            [:body
                             [:div#main_contain
                              [:div {:class "latest"} "Latest Board Activity"]]])

          :component/complete (html
                            [:body
                             [:div#main_contain
                              [:div {:class "latest"} "Complete Index of Boards"]]])

          })


(defn comment-input [term]
  (let [crown (int (Math/floor (* 1000 (rand))))]
                             (html (form-to
                                     {:enctype "multipart/form-data"}
                                     [:post "/upload-image"]
                                     (hidden-field {:value crown} "capval")
                                     (hidden-field {:value term} "boardname")
                                     (text-area {:placeholder (str "post new thread in " term)} "post_content")
                                     (text-field {:placeholder crown} "captcha")
                                     [:input {:id "fileuploadfield" :type "file" :placeholder "image" :name "upload-file" }]
                                     (submit-button {:class "btn"
                                                     :id "post_submit"
                                                     :onSubmit (submit-post crown)} "Post")))))

(defn comment-input-with-tid [term tid]
  (let [crown (int (Math/floor (* 1000 (rand))))]
                             (html (form-to
                                     {:enctype "multipart/form-data"}
                                     [:post "/upload-image"]
                                     (hidden-field {:value crown} "capval")
                                     (hidden-field {:value term} "boardname")
                                     (hidden-field {:value tid} "thread-id")
                                     (text-area {:placeholder (str "post to " term "/" tid)} "post_content")
                                     (text-field {:placeholder crown} "captcha")
                                     [:input {:id "fileuploadfield" :type "file" :placeholder "image" :name "upload-file" }]
                                     (submit-button {:class "btn"
                                                     :id "post_submit"
                                                     :onSubmit (submit-post crown)} "Post")))))

(defn html-recents
  "Generates a footer element given a collection of folds (board names)"
  [coll]
  (html [:div#marking
         [:div#topfolds.main
         (for [fold coll]
           [:a.fold {:href (str "/" fold)} (str "/" fold)])]]))


(defn draw-threads-for-page
  "Returns HTML rendering of threads coll"
  [term]

  ;(println "^ " (get-threads-by-page term))

  (html
    [:div.threads
    (let [threads (get-threads-by-page term)]
       (for [thread threads]
         (do
         ;(println "^^ " thread)
         [:div.thread {:thread-id thread}
          [:a {:href (str "/" term "/" thread)}
           (let [post-ids (get-posts-by-thread thread)]
             (for [pid post-ids]
               (do
               ;(println "^^^ " pid)
               (let [post-map (first (get-post-by-id pid))]
                 [:div.post
                   [:div.post_content (:content post-map)]
                   [:div.post_timestamp "posted at " (:timestamp post-map)]
                  (if (not (= nil (:image-url post-map)))
                 [:img.thumbnail {:src (:image-url post-map)}])]))))]])))]))




(defn draw-posts-for-thread
  "Returns HTML rendering of all the posts in a given thread"
  [tid]
  (html
    [:div.thread {:id tid}
     ;(println "get posts by thread tid" (get-posts-by-thread tid))
         (let [post-ids (get-posts-by-thread tid)]
           (for [pid post-ids]
             (let [post-map (first (get-post-by-id pid))]
               (do
                 ;(println "^x^ " pid)
                 ;(println "^xx^ " (get-post-by-id pid))
                 ;(println "^xxx^ " (first (get-post-by-id pid)))

               [:div.post
                 [:div.post_content (:content post-map)]
                 [:div.post_timestamp "posted at " (:timestamp post-map)]

                (if (not (= nil (:image-url post-map)))
                 [:img.threadimg {:src (:image-url post-map)}])]))))]))



(def folds ["nature" "pomp" "waves" "wigwam"])

(defn file-upload-progress [request, bytes-read, content-length, item-count]
  ;(println "+ " bytes-read)
  )



(defroutes hachiba-routes
  (GET "/" [] (concat gtag
                      (html [:title "Practical Human - Image and Discussion Board"])
                      (:component/search cm)
                      (:component/header cm)
                      (:component/menu cm)
                      (:component/latest cm)
                      (html-recents (take 100 (distinct @last-modified)))))

  (route/resources "/uploads/")

  (POST "/" [params :as params]
    (let [desired-url (:form-params params)
          sanitized (clojure.string/replace desired-url #"[^a-zA-Z0-9\s]" "")]
      {:status 302
       :headers {"Location" sanitized}
       :body ""}))

  (mp/wrap-multipart-params
    (POST "/upload-image" [params :as params]
      ;(println "* file up /post")
      ;(println "** " params)

    (let [fp (:params params)
          capval (get fp "capval")
          boardname (get fp "boardname")
          content (get fp "post_content")
          captcha (get fp "captcha")
          sanitized content ;(clojure.string/replace content #"[^a-zA-Z0-9\s.()]" "")
          thread-id (get fp "thread-id")
          timestamp (quot (System/currentTimeMillis) 1000)

          mpp (:multipart-params params)
          file-map (get mpp "upload-file")
          temp-file (:tempfile file-map)
          size (:size file-map)
          file-name (:filename file-map)
          file-type (:content-type file-map)
          extension (case file-type "image/jpeg" ".jpg" "image/png" ".png" "image/gif" ".gif" nil)
          new-file-name (str "img" (quot (System/currentTimeMillis) 1000) (rand-int 4) extension)
          image-url (str "/uploads/" new-file-name)]

      (if (= capval captcha)
        (do
        ;  (println "+ " file-name)
        ;  (println "++ " params)
        ;  (println "+++ " boardname)
        ;  (println "++++ " thread-id)
        ;  (println "+++++ " content)
        ;  (println "++++++ " temp-file)
        ;  (println "+++++++ " file-name)
        ;  (println "++++++++ " file-type)
        ;  (println "+++++++++ " extension)

          (let [tid-after-post (new-post boardname thread-id content  (if (not (nil? extension)) image-url nil))]



        ;(println "*** " file-name file-type size)
        (if (not (nil? extension))
          (do
            (io/copy (io/file temp-file) (io/file (str "resources/public/uploads/" new-file-name)))
            (concat gtag
              (html [:title (str "ph/" boardname)])
              (:component/search cm)
              (:component/header cm)
              (:component/menu cm)
              [:div#topterm (str "File upload success.")]
              [:meta {:http-equiv "refresh" :content (str "3;URL='/" boardname "/" tid-after-post "'")}]
              [:div#img-link [:a {:href image-url} new-file-name]]
              [:div#post-link [:a {:href (str "/" boardname "/" tid-after-post)} (str "/" boardname "/" tid-after-post)]])))
          ;else there no file to upload
          (do
            {:status 302
             :headers {"Location" (str "/" boardname "/" tid-after-post)}
             :body ""})))

        ;else invalid captcha
        (do
          (concat gtag
              (html [:title "Practical Human - Captcha Invalid."])
              (:component/search cm)
              (:component/header cm)
              (:component/menu cm)
              (html [:div#topterm (str "Invalid Captcha.")]))))))
    {:progress-fn file-upload-progress})


  (GET "/user" [params :as params] (str "user!" params))
  (GET "/about" [params :as params]
       (concat        gtag
                      (html [:title (str "Practical Human - About")])
                      (:component/search cm)
                      (html [:div#topterm (str "about practicalhuman.org/")])
                      (:component/header cm)
                      (html [:div#about
                             [:div.heading "Freedom of Speech."]
                             [:div.disclaim "Please put not safe for work in topics labeled nsfw as a courtesy to others"]
                             [:div.details "You can upload .jpg .png and .gif image files."]
                             [:div.details "Please upload images of 200x200 px or larger."]
                             [:div.details "Contact us by e-mail," [:a {:href "mailto:team@practicalhuman.org"} "team@practicalhuman.org"]]
                             [:div.source [:p "Source Code available for personal, non-commercial use."][:p [:a {:href "http://github.com/sova/hachiba"} "[ view ph source on github ]"]]]
                             [:div.footing [:img.footingimg {:src "clojure-logo.png"}] [:span.footingclj "Powered by Clojure"]]])
                      (:component/menu cm)))

  (GET "/fresh" [params :as params] (str "fresh!" params))
  (GET "/favorites" [params :as params] (str "favorites!" params))
  (GET "/logout" [params :as params] (str "logout!" params))
  (GET "/index" [params :as params]
    (let [pagenames (map :pagename @page-threads)]
              (concat gtag
                      (html [:title "Practical Human - Image and Discussion Board"])
                      (:component/search cm)
                      (:component/header cm)
                      (:component/menu cm)
                      (:component/complete cm)
                      (html-recents pagenames))))

  (GET "/:term" [term]
              (concat gtag
                      (html [:title (str "ph/" term)])
                      (:component/search cm)
                      (html [:div#topterm (str "now browsing /"   term)])
                      (:component/header cm)

                      (draw-threads-for-page term)
                      (comment-input term)
                      (:component/menu cm)

                      ;(html-footer folds)
                      ))


  (GET "/:term/" [term]
              (concat gtag
                      (html [:title (str "ph/" term "/")])
                      (:component/search cm)
                      (html [:div#topterm (str "now browsing /" term "/")])
                      (:component/header cm)

                      (draw-threads-for-page term)
                      (comment-input term)
                      (:component/menu cm)
                      ;(html-footer folds)
                      ))

  (GET "/:term/:tid" [term tid]
              (concat gtag
                      (html [:title (str "ph/" term "/" tid)])
                      (:component/search cm)
                      (html [:div#topterm "now browsing /" [:a {:href (str "/" term)} term] (str "/" tid)])
                      (:component/header cm)

                      ;(println "gpbt" (get-posts-by-thread tid))
                      (draw-posts-for-thread tid)
                      ;(println "tid" tid)
                      (comment-input-with-tid term tid)
                      (:component/menu cm)
                      ;(html-footer folds)
                      ))


  (route/not-found
        (concat gtag
              (html [:title "Practical Human - 404"])
              (:component/search cm)
              (:component/header cm)
              (:component/menu cm)
              (html [:div#topterm (str "404 Resource Not Found")]))))


(def app
  (-> (wrap-defaults hachiba-routes api-defaults)
      (wrap-resource "public")))


