(defproject validator "0.1.1-SNAPSHOT"
  :description "data validation library"
  :url "https://github.com/Opetushallitus/validaattori"
  :license {:name "EUPL"
            :url "http://www.osor.eu/eupl/"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3126"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :profiles     {:dev {:plugins [[com.cemerick/austin "0.1.6"]
                                 [lein-midje "3.1.1"]
                                 [lein-kibit "0.0.8"]
                                 [jonase/eastwood "0.2.1"]
                                 [lein-cljsbuild "1.0.5"]
                                 [com.birdseye-sw/lein-dalap "0.1.0"]]
                       :dependencies [[midje "1.6.3"]]}
                 :hakurekisteri ^:leaky {:source-paths ["src/clojure" "hakurekisteri/src"]
                                         :aot [hakurekisteri.api]
                                 :test-paths ["test" "hakurekisteri/test"]
                                 :name "hakurekisteri-validation"
                                 :dalap-rules "hakurekisteri/dalap_rules.clj"
                                 :filespecs [{:type :path :path "prod-js/hakurekisteri-validator.min.js"}]

                                 :cljsbuild {
                                             :builds [{:id "dev"
                                                       :source-paths ["hakurekisteri/cljs" "gen-cljs"]
                                                       :compiler {
                                                                  :output-dir "target/javascripts"
                                                                  :output-to "target/javascripts/hakurekisteri-validator.js"  ; default: target/cljsbuild-main.js
                                                                  :optimizations :none
                                                                  :pretty-print true
                                                                  :source-map true}}
                                                      {:id "prod"
                                                       :source-paths ["hakurekisteri/cljs" "gen-cljs"]
                                                       :compiler {:output-dir "prod-js"
                                                                  :output-to "prod-js/hakurekisteri-validator.min.js"
                                                                  :optimizations :advanced
                                                                  :pretty-print false}}]}}}
  :deploy-repositories {"snapshots" {:url "https://artifactory.opintopolku.fi/artifactory/oph-sade-snapshot-local"
                                     :username :env/artifactory_username
                                     :password :env/artifactory_password}
                        "releases" {:url "https://artifactory.opintopolku.fi/artifactory/oph-sade-release-local"
                                    :username :env/artifactory_username
                                    :password :env/artifactory_password}}
  :hooks [leiningen.dalap]
  :aliases {"hr" ["with-profile" "+hakurekisteri"]
            "hr-prod" ["hr" "cljsbuild" "once" "prod"]
            "hr-dev" ["hr" "cljsbuild" "once" "dev"]}
  :cljsbuild {:builds [{:source-paths ["gen-cljs"]
                        :compiler {:output-to "target/javascripts/validator.js"  ; default: target/cljsbuild-main.js
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
