# pdf-2-images

Clojure wrapper for the PDFBox that converts a page range of a PDF document to images.

**Why forked?** It seems the maintainer of the original [repo](https://github.com/igmonk/pdf-to-images) is no longer available or not willing to commit new changes.

## Quickstart

For installation, add the following dependency to your `project.clj` file:

    [org.clojars.roboli/pdf-2-images "0.2.0"]

Import namespace example:

```clojure
(ns hello-world.core
  (:require [pdf-2-images.core :as pdf]))
```

You can choose one of the three predefined handlers, that will let you convert your PDF's page or pages to an image:

* `image-to-image`: Returns a list of [buffered images](https://javadoc.io/static/org.apache.pdfbox/pdfbox/2.0.29/org/apache/pdfbox/rendering/PDFRenderer.html#renderImageWithDPI-int-float-).
* `image-to-byte-array`: Returns a list of [byte arrays](https://docs.oracle.com/javase/8/docs/api/java/io/ByteArrayOutputStream.html#toByteArray--), one per image.
* `image-to-file`: Returns a list of paths (strings), one per image.

*Or pass your own custom handler (more below).*

Basic usage example:

```clojure
(let [image-paths (pdf/pdf-2-images pdf/image-to-file :pdf-file (clojure.java.io/file "path-to-pdf"))]
  (prn (str "Images count: " (count image-paths)))
  (map prn image-paths))

;; "Images count: n"
;; "path-to-image-0"
;; "path-to-image-1"
;; ...
;; "path-to-image-n-1"
```

The same with key-value pair parameter - pathname will be used if pdf-file is not specified (= nil):

```clojure
(let [image-paths (pdf/pdf-2-images pdf/image-to-file :pathname "path-to-pdf")]
  (prn (str "Images count: " (count image-paths)))
  (map prn image-paths))

;; "Images count: n"
;; "path-to-image-0"
;; "path-to-image-1"
;; ...
;; "path-to-image-n-1"
```

With-options usage example:

```clojure
(let [image-paths (pdf/pdf-2-images pdf/image-to-file
                                    :pdf-file (clojure.java.io/file "path-to-pdf")
                                    :start-page 0
                                    :end-page 1
                                    :dpi 100
                                    :quality 1
                                    :ext "jpg")]
  (prn (str "Images count: " (count image-paths)))
  (map prn image-paths))

;; "Images count: 2"
;; "path-to-image-0"
;; "path-to-image-1"
```

## Usage

```clojure
(pdf-2-images handler :option-1-key option-1-val :option-2-key option-2-val ...)
```

Where options:

* `:page`: Page to convert to image, takes precedence over *:start-page* and *:end-page*
* `:start-page`: The start page, defaults to 0
* `:end-page`: The end page, defaults to PDF's pages length - 1 or Integer/MAX_VALUE
* `:dpi`: Screen resolution, defaults to 300
* `:quality`: Quality to be used when compressing the image (0 < quality < 1), defaults to 1
* `:ext`: The target file format, defaults to png
* `:pdf-file`: A PDF java.io.File, takes precedence over *:pathname*
* `:pathname`: Path to the PDF file, used if *:pdf-file* is not specified (= nil)

## Custom Handlers

Basically, pass in a function expecting a map:

```clojure
(your-custom-handler m)
```

Where `m` has the following keys with corresponding value:

* `:image`: A [buffered image](https://javadoc.io/static/org.apache.pdfbox/pdfbox/2.0.29/org/apache/pdfbox/rendering/PDFRenderer.html#renderImageWithDPI-int-float-) of the current page
* `:image-index`: The current page index
* `:ext`: The image file extension
* `:dpi`: Screen resolution
* `:quality`: Quality to be used when compressing the image
* `:base-path`: The PDF file base path

There are no constraints for the returned value.

## License

* Copyright © 2016 Igor Moiseyenko
* Copyright © 2023 Roberto Oliveros

Distributed under the Eclipse Public License version 1.0.
