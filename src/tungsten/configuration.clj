(ns tungsten.configuration
  (:require [aero.core :as aero]
            [schema.core :as schema]
            [tungsten.logger :as logger]
            [tungsten.database :as db]
            [tungsten.webserver :as webserver]
            [tungsten.application :as app])
  (:import (com.mongodb MongoSocketOpenException)))

(def Configuration-Schema
      "Configuration file schema"
  {:log-configuration {:mode (schema/enum :std-out :file)
                       (schema/optional-key :file-path) schema/Str
                       :level schema/Keyword}
   :mongo-db {:host schema/Str
              :port schema/Int
              :username schema/Str
              :password schema/Str
              :database-name schema/Str
              :collection-name schema/Str}
   :rest-server {:port schema/Int
                 (schema/optional-key :max-threads) schema/Int
                 (schema/optional-key :min-threads) schema/Int
                 (schema/optional-key :host) schema/Str
                 (schema/optional-key :async?) schema/Bool}})

(defn read-configuration [config-path]
  (let [configuration (aero/read-config config-path)]
    (when (schema/validate Configuration-Schema configuration)
      configuration)))

(defn set-configuration [app-config system]
  (logger/set-log-config (:log-configuration app-config))
  (let [application
        {:database (db/set-db-config (:mongo-db app-config) system)
         :webserver (webserver/set-webserver-config
                      (:rest-server app-config) system)}]
    (app/map->Tungsten application)))