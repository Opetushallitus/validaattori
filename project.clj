(defproject validator "0.1.0-SNAPSHOT"
  :description "data validation library"
  :url "http://example.com/FIXME"
  :license {:name "EUPL"
            :url "http://www.osor.eu/eupl/"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles     {:dev {:plugins [[lein-midje "3.1.1"]
                                 [lein-kibit "0.0.8"]
                                 [jonase/eastwood "0.2.1"]]
                       :dependencies [[midje "1.6.3"]]}})
