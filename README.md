# pdf-2-images

Clojure wrapper for the PDFBox that converts a page range of a PDF document to images.

**Why forked?** It seems the maintainer of the original [repo](https://github.com/igmonk/pdf-to-images) is no longer available or not willing to commit new changes.

## Installation

Add the following dependency to your `project.clj` file:

    [org.clojars.roboli/pdf-2-images "0.2.0"]

## Usage

Import namespace example:

```clojure
(ns hello-world.core
  (:require [pdf-2-images.core :as pdf]))
```

You can choose from three predefined handlers, that will let you convert your PDF's page (or pages) to an image and return them as a list of:

Handler | Output
---|---
image-to-image | [Buffered image](https://javadoc.io/doc/org.apache.pdfbox/pdfbox/2.0.29/index.html)
image-to-byte-array | [Byte array](https://docs.oracle.com/javase/8/docs/api/java/io/ByteArrayOutputStream.html#toByteArray--)
image-to-file | Path to file

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

## License

* Copyright © 2016 Igor Moiseyenko
* Copyright © 2023 Roberto Oliveros

Distributed under the Eclipse Public License version 1.0.
