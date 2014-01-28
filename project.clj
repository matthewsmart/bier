(defproject bier "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/tools.cli "0.3.1"]]
  :main ^:skip-aot bier.core
  :target-path "target/%s"
  :aot [bier.core bier.Recipe]
  :profiles {:uberjar {:aot :all}})
