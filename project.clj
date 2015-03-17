(defproject validator "0.1.0-SNAPSHOT"
  :description "data validation library"
  :url "http://example.com/FIXME"
  :license {:name "EUPL"
            :url "http://www.osor.eu/eupl/"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2913"]]
  :profiles     {:dev {:plugins [[lein-midje "3.1.1"]
                                 [lein-kibit "0.0.8"]
                                 [jonase/eastwood "0.2.1"]
                                 [lein-cljsbuild "1.0.5"]
                                 [com.birdseye-sw/lein-dalap "0.1.0"]]
                       :dependencies [[midje "1.6.3"]]}
                 :hakurekisteri {
                                   :source-paths ["src" "hakurekisteri/src"]
                                   :name "hakurekisteri-validation"
                                   :dalap-rules "hakurekisteri/dalap_rules.clj"
                                   :cljsbuild {
                                               :builds [{
                                                         ; The path to the top-level ClojureScript source directory:
                                                         :source-paths ["cljs"]
                                                         ; The standard ClojureScript compiler options:
                                                         ; (See the ClojureScript compiler documentation for details.)
                                                         :compiler {
                                                                    :output-to "target/javascripts/hakurekisteri-validator.js"  ; default: target/cljsbuild-main.js
                                                                    :optimizations :whitespace
                                                                    :pretty-print true}
                                                         }]}}}
  :hooks [leiningen.dalap leiningen.cljsbuild]
  :cljsbuild {
    :builds [{
        ; The path to the top-level ClojureScript source directory:
        :source-paths ["cljs"]
        ; The standard ClojureScript compiler options:
        ; (See the ClojureScript compiler documentation for details.)
        :compiler {
          :output-to "target/javascripts/validator.js"  ; default: target/cljsbuild-main.js
          :optimizations :whitespace
          :pretty-print true}}]})
