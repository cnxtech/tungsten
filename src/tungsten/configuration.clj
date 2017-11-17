(ns tungsten.configuration
  (:require [aero.core :as aero]
            [schema.core :as schema]
            [tungsten.logger :as logger]
            [tungsten.database :as db]))

(def Configuration-Schema
      "Configuration file schema"
  {:log-configuration {:log-mode (or (schema/eq :std-out)
                                     (schema/eq :log-file))
                       (schema/optional-key :log-file-path) schema/Str}
   :mongo-db {:host schema/Str
              :port schema/Int
              :user schema/Str
              :password schema/Str}})

(defn read-configuration [config-path]
  (let [configuration (aero/read-config config-path)]
    (when (schema/validate Configuration-Schema configuration)
      configuration)))

(defn set-configuration [app-config system]
  (logger/set-log-configuration (:log-configuration app-config))
  (db/set-db-configuration (:mongo-db app-config) system))