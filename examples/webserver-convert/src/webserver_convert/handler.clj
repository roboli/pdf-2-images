(ns webserver-convert.handler
  (:require [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as response]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware [multipart-params :as mp]]
            [pdf-2-images.core :as pdf]))

(defroutes app-routes
  (GET "/" [] (str "<p><b>POST</b> /convert/:page</p>"
                   "<p>Send your PDF as the 'file' field in a multipart/form-data."
                   "<br />Where <i>:page</i> is the page number (zero-indexed) you want to convert.</p>"))

  (mp/wrap-multipart-params
   (POST "/convert/:page"
         {:keys [params]}
         (let [page (Integer/parseInt (get params :page))
               file (get params "file")
               out-array (first (pdf/pdf-2-images pdf/image-to-byte-array
                                                  :pdf-file (file :tempfile)
                                                  :start-page (dec page)
                                                  :end-page page
                                                  :dpi 72
                                                  :quality 0.5))]
           (with-open [in (java.io.ByteArrayInputStream. out-array)]
             (-> (response/response in)
                 (response/header "Content-Disposition" "filename=test.png")
                 (response/header "Content-Length" (count out-array))
                 (response/content-type "image/png")))))))

(def app
  (wrap-defaults app-routes api-defaults))
