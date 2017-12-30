(ns tungsten.database
  (:require [monger.core :as monger]
            [monger.collection :as monger-coll]
            [monger.credentials :refer [create]]
            [tungsten.logger :as logger])
  (:import (com.mongodb MongoSocketOpenException MongoTimeoutException)))

(defprotocol Database
  "Database definition"
  (connect [x] "Connects to a database"))


(def default-mongo-db-options
  {:socket-timeout 10
   :auto-connect-retry false
   :max-auto-connect-retry-time 1000
   :max-wait-time 1000
   :connect-timeout 1000})

(defn test-mongo-db-connection [conn]
  (monger/get-db-names conn)
  conn)

(defrecord MongoDB [username database-name collection-name options password
                    host port system]
  Database
  (connect [db]
    (let [db-options (monger/mongo-options default-mongo-db-options)
          address (monger/server-address "127.0.0.1" 27017)]
      (try (-> (monger/connect address db-options)
               (test-mongo-db-connection))
           (logger/log :info "Database started.")
           (catch MongoTimeoutException e
             (logger/log :critical "Could not connect to database.")
             (System/exit 3))))))

(defn insert-doc [document system]
  (monger-coll/insert-and-return
    (:db @system) (-> @system :db :collection-name) document))

(defn create-database [db-config system]
  (let [credentials (create (:username db-config)
                            (:database-name db-config)
                            (:password db-config))]
    (map->MongoDB (assoc db-config :system system))))
