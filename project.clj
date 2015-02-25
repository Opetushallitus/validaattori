(defproject validator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles     {:dev {:plugins [[lein-midje "3.1.1"]
                                 [lein-kibit "0.0.8"]
                                 [jonase/eastwood "0.2.1"]]
                       :dependencies [[midje "1.6.3"]]}})
