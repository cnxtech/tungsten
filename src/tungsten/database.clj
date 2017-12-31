(ns tungsten.database
  (:require [monger.core :as monger]
            [monger.collection :as monger-coll]
            [monger.credentials :refer [create]]
            [tungsten.logger :as logger])
  (:import (java.net ConnectException)
           (com.mongodb MongoSocketOpenException)))

(defprotocol Database
  "Database definition"
  (connect [x] "Connects to a database"))

(defrecord MongoDB [username database-name password host port system]
  Database
  (connect [db]
    (swap! system assoc :db (monger/get-db (monger/connect db) "tungsten"))))

(def collection-name "tungsten")

(defn insert-doc [document system]
  (monger-coll/insert-and-return (:db @system) collection-name document))

(defn set-db-config [db-config system]
  (let [credentials (create (:username db-config)
                            (:database-name db-config)
                            (:password db-config))]
    (map->MongoDB (assoc db-config :system system))))
