(ns tungsten.core
  (:require [tungsten.configuration :as config]
            [tungsten.logger :as logger]
            [tungsten.database :as db]
            [tungsten.webserver :as webserver]
            [ring.adapter.jetty :as jetty]
            [clojure.core.async :as a]
            [tungsten.application :as application])
  (:gen-class))

(defn -main
  [& args]
  (let [app-config (config/read-configuration "/Users/aaronsteed/GitHub/tungsten/config.edn")]
    (.start (config/set-configuration app-config application/system-atom))
    (logger/log "Application started")))