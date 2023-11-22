(ns pdf-2-images.core
  (:require [clojure.java.io :as io])
  (:import [org.apache.pdfbox.pdmodel PDDocument]
           [org.apache.pdfbox.rendering PDFRenderer]
           [org.apache.pdfbox.rendering ImageType]
           [org.apache.pdfbox.tools.imageio ImageIOUtil]
           (java.io ByteArrayOutputStream)))

(defn- range-pages [total start end]
  (if (= start end)
    (list start)
    (let [totalp (dec total)
          endp   (if (< totalp end) totalp end)]
      (range start (inc endp)))))

(defn image-to-image [{image :image}] image)

(defn image-to-byte-array
  [{image :image ext :ext dpi :dpi quality :quality}]
  (let [baos (ByteArrayOutputStream.)]
    (try
      (ImageIOUtil/writeImage image ext baos dpi quality)
      (.flush baos)
      (.toByteArray baos)
      (finally
        (if (not= baos nil) (.close baos))))))

(defn image-to-file
  [{image :image image-index :image-index ext :ext dpi :dpi quality :quality base-path :base-path}]
  (let [image-pathname (str base-path "-" image-index "." ext)]
    (with-open [baos (ByteArrayOutputStream.)
                out  (io/output-stream (io/file image-pathname))]
      (ImageIOUtil/writeImage image ext baos dpi quality)
      (.flush baos)
      (.write out (.toByteArray baos))
      image-pathname)))

(defn pdf-2-images
  "Converts a page range of a PDF document to images using one of the defined image handlers:

  image-to-image
  image-to-byte-array
  image-to-file

  Or pass in a custom one. Returns a sequence consisting of the images, byte arrays or pathnames
  depending on the selected image handler.

  Options are key-value pairs and may be one of:
    :page       - Page to convert to image, takes precedence over :start-page and :end-page
    :start-page - The start page, defaults to 0
    :end-page   - The end page, defaults to PDF's pages length - 1 or Integer/MAX_VALUE
    :dpi        - Screen resolution, defaults to 300
    :quality    - Quality to be used when compressing the image (0 < quality < 1), defaults to 1
    :ext        - The target file format, defaults to png
    :pdf-file   - A PDF java.io.File, takes precedence over :pathname
    :pathname   - Path to the PDF file, used if :pdf-file is not specified (= nil)"
  [image-handler & {:keys [page start-page end-page dpi quality ext pdf-file pathname]
                    :or {start-page (if page page 0)
                         end-page (if page page (Integer/MAX_VALUE))
                         dpi 300
                         quality 1
                         ext "png"}}]

  (let [pdf-file     (if pdf-file pdf-file (io/file pathname))
        pd-document  (PDDocument/load pdf-file)
        pdf-renderer (PDFRenderer. pd-document)
        total-pages  (.getNumberOfPages pd-document)
        page-range   (range-pages total-pages start-page end-page)]
    (try
      (doall
       (map
        (fn [page-index]
          (let [image (.renderImageWithDPI pdf-renderer page-index dpi ImageType/RGB)]
            (image-handler {:image image
                            :image-index page-index
                            :ext ext
                            :dpi dpi
                            :quality quality
                            :base-path (.getAbsolutePath pdf-file)})))
        page-range))
      (finally
        (if (not= pd-document nil) (.close pd-document))))))
