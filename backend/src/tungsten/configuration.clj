(ns tungsten.configuration
  (:require [aero.core :as aero]
            [schema.core :as schema]
            [tungsten.logger :as logger]
            [tungsten.database :as db]
            [tungsten.webserver :as webserver]
            [tungsten.application :as app]
            [tungsten.schema.configuration-schema :as config-schema]))

(defn set-configuration [app-config system]
  (logger/set-log-config (:log-configuration app-config))
  (let [tungsten-config
        {:database (db/create-database (:mongo-db app-config) system)
         :webserver (webserver/create-webserver
                      (:rest-server app-config) system)}]
    (app/map->Tungsten tungsten-config)))

(defn read-configuration [config-path]
  (let [configuration (aero/read-config config-path)]
    (if (schema/validate config-schema/Configuration-Schema configuration)
      (set-configuration configuration app/system-atom)
      (println "Configuration invalid"))))