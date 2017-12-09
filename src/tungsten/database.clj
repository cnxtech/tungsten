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

(defrecord MongoDB [username database-name password host port]
  Database
  (connect [x]
    ; Do something here
    nil))

(defn insert-doc [document collection system]
  (monger-coll/insert-and-return (:db @system) collection document))

(defn set-db-configuration [db-config system]
  (let [credentials (create (:username db-config)
                            (:database-name db-config)
                            (:password db-config))]
    (swap! system assoc :db (monger/get-db
                              (monger/connect db-config)
                              "tungsten"))))
