(defproject tungsten "0.0.1-SNAPSHOT"
  :description "Instagram bots as a service"
  :url "https://github.com/aaronsteed/tungsten"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/core.async "0.3.465"]
                 [ring/ring-jetty-adapter "1.6.2"]
                 [ring-json-params "0.1.3"]
                 [compojure "1.6.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [aero "1.1.2"]
                 [prismatic/schema "1.1.7"]
                 [clj-json "0.5.3"]
                 [http-kit "2.2.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.novemberain/monger "3.1.0"]
                 [ring "1.6.1"]]
  :main tungsten.core
  :target-path "target/"
  :profiles {:uberjar {:aot :all}})
