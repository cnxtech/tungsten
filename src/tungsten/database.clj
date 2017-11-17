(ns tungsten.database
  (:require [monger.core :as monger]
            [monger.credentials :refer [create]]))

(defn set-db-configuration [db-config system]
  (let [credentials (create (:username db-config)
                            (:database-name db-config)
                            (:password db-config))]

    (swap! system assoc :db (monger/connect {:host (:host db-config)
                                             :port (:port db-config)}))))
