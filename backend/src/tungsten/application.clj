(ns tungsten.application
  (:require [taoensso.timbre :as timbre])
  (:import [tungsten.database Database]
           [tungsten.webserver Web_Server]))

(defonce system-atom (atom {}))

(defprotocol Application
  (start [app] "Starts an applicaton"))

(defrecord Tungsten [database webserver]
  Application
  (start [app]
    (.connect ^Database database)
    (.start ^Web_Server webserver)
    (swap! system-atom (fn [sys-atom db webserver]
                         (-> sys-atom
                             (assoc :database db)
                             (assoc :webserver webserver))) database webserver)
    (timbre/info "Application started.")))