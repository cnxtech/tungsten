(ns tungsten.schema.configuration-schema
  (:require [schema.core :as schema]))

(def Rest-Server-Configuration
  {:port schema/Int
   (schema/optional-key :host) schema/Str
   (schema/optional-key :async?) schema/Bool})

(def Mongo-DB-Configuration
  {:host schema/Str
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
    (schema/optional-key :connect-timeout) schema/Int}})

(def Log-Configuration
  {:mode (schema/enum :std-out :file)
   (schema/optional-key :file-path) schema/Str
   :level schema/Keyword
   :file-name schema/Str})

(def Configuration-Schema
  {:log-configuration Log-Configuration
   :mongo-db Mongo-DB-Configuration
   :rest-server Rest-Server-Configuration})