(ns tungsten.core
  (:use compojure.core)
  (:use ring.middleware.json-params)
  (:use ring.adapter.jetty)
  (:require [tungsten.configuration :as config]
            [tungsten.logger :as logger]
            [clj-json.core :as json]
            [tungsten.database :as db])
  (:gen-class)
  (:import (com.mongodb MongoSocketOpenException)))

(defn json-response [data & [status]]
  {:status  (or status 200)
   :headers {"Content-Type" "application/json"}
   :body    (json/generate-string data)})

(defroutes handler
           (GET "/" []
             (json-response {"hello" "world"}))

           (PUT "/:user" [user]
             (json-response {"hello" user})))
(def app
 (-> handler
    wrap-json-params))

(def system
  "Used for storing service objects and connections to services"
  (atom {}))

(defn -main
  [& args]
  (try
    (let [app-config (config/read-configuration "/Users/aaronsteed/GitHub/tungsten/config.edn")]
      (try
        (config/set-configuration app-config system)
        (catch MongoSocketOpenException e (logger/log "we're fucked")))
      (db/insert-doc {:me "you"} "tungsten" system))
    (catch Exception e (logger/log :critical (.getMessage e)))))