(ns tungsten.configuration
  (:require [aero.core :as aero]
            [schema.core :as schema]
            [tungsten.logger :as logger]
            [tungsten.database :as db]
            [tungsten.webserver :as webserver]
            [tungsten.application :as app]))

(def Configuration-Schema
      "Configuration file schema"
  {:log-configuration {:mode (schema/enum :std-out :file)
                       (schema/optional-key :file-path) schema/Str
                       :level schema/Keyword
                       :file-name schema/Str}
   :mongo-db {:host schema/Str
              :port schema/Int
              :username schema/Str
              :password schema/Str
              :database-name schema/Str
              :collection-name schema/Str
              (schema/optional-key :options)
              {(schema/optional-key :auto-connect-retry) schema/Bool
               (schema/optional-key :connections-per-host) schema/Int
               (schema/optional-key :socket-timeout) schema/Int
               (schema/optional-key :max-auto-connect-retry-time) schema/Int
               (schema/optional-key :max-wait-time) schema/Int
               (schema/optional-key :connect-timeout) schema/Int}}
   :rest-server {:port schema/Int
                 (schema/optional-key :host) schema/Str
                 (schema/optional-key :async?) schema/Bool}})

(defn read-configuration [config-path]
  (let [configuration (aero/read-config config-path)]
    (when (schema/validate Configuration-Schema configuration)
      configuration)))

(defn set-configuration [app-config system]
  (logger/set-log-config (:log-configuration app-config))
  (let [tungsten-config
        {:database (db/create-database (:mongo-db app-config) system)
         :webserver (webserver/create-webserver
                      (:rest-server app-config) system)}]
    (app/map->Tungsten tungsten-config)))