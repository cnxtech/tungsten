(ns tungsten.application
  (:import [tungsten.database Database]
           [tungsten.webserver Web_Server])
  (:require [tungsten.logger :as logger]))

(defonce system-atom (atom {}))

(defprotocol Application
  "Application definition"
  (start [app] "Starts an applicaton"))

; Defrecord of system components (Webserver, database etc...)
(defrecord Tungsten [database webserver]
  Application
  (start [app]
    (.connect ^Database database)
    (.start ^Web_Server webserver)
    (swap! system-atom (fn [sys-atom db webserver]
                         (-> sys-atom
                             (assoc :database db)
                             (assoc :webserver webserver))) database webserver)
    (logger/log :info "Application started.")))
