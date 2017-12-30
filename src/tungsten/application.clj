(ns tungsten.application
  (:import [tungsten.database Database]
           [tungsten.webserver Web_Server]))

(defonce system-atom
         (atom {}))

(defprotocol Application
  "System definition"
  (start [app] "Starts an applicaton"))

; Defrecord of system components (Webserver, database etc...)
(defrecord Tungsten [database webserver]
  Application
  (start [app]
    (.connect ^Database database)
    (.start ^Web_Server webserver)
    (swap! system-atom (fn [atom db webserver]
                         (-> atom
                             (assoc :db db)
                             (assoc :webserver webserver))) database webserver)))
