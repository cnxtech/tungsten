(ns tungsten.webserver
  (:use compojure.core)
  (:use compojure.handler)
  (:use ring.middleware.json-params)
  (:require [clj-json.core :as json]
            [org.httpkit.server :as httpkit]
            [tungsten.logger :as logger]))

(defn json-response [data & [status]]
  {:status  (or status 200)
   :headers {"Content-Type" "application/json"}
   :body    (json/generate-string data)})

(defroutes app-routes
           (GET "/" []
             (json-response {"hello" "world"}))

           (PUT "/:user" [user]
             (json-response {"hello" user})))

(def rest-app
  (wrap-json-params app-routes))

(defn start-server [app config]
  (let [server (httpkit/run-server (site #'rest-app) {:port 8080})]
    server))

(defprotocol Web-Server
  "Webserver definition"
  (start [server] "Starts a webserver"))

(defrecord Rest-Server [jetty-config handler]
  Web-Server
  (start [rest-server]
    (start-server (:handler rest-server) jetty-config)
    (logger/log :info "REST Server started.")))

(defn create-webserver [rest-server-config system]
  (-> rest-server-config
      (Rest-Server. rest-app)))