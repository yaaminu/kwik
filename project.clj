(defproject kwik "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "3.9.1"] [org.clojure/clojure "1.8.0"] [http-kit "2.2.0"] [compojure "1.6.1"] [ring/ring-defaults "0.3.2"]]
  :main ^:skip-aot kwik.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
