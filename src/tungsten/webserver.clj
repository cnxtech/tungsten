(ns tungsten.webserver
  (:use compojure.core)
  (:use compojure.handler)
  (:use ring.middleware.json-params)
  (:require [clj-json.core :as json]
            [ring.adapter.jetty :as jetty]))

(defn json-response [data & [status]]
  {:status  (or status 200)
   :headers {"Content-Type" "application/json"}
   :body    (json/generate-string data)})

(defroutes handler
           (GET "/" []
             (json-response {"hello" "world"}))

           (PUT "/:user" [user]
             (json-response {"hello" user})))

(def rest-app
  (wrap-json-params handler))

(defn start-server [app config]
  (jetty/run-jetty (var rest-app) config))

(defprotocol Web-Server
  "Webserver definition"
  (start [server] "Starts a webserver"))

(defrecord Rest-Server [jetty-config handler]
  Web-Server
  (start [rest-server]
    (start-server (:handler rest-server) jetty-config)))

(defn set-webserver-config [rest-server-config system]
  (-> rest-server-config
      (assoc :join? false)
      (Rest-Server. rest-app)))