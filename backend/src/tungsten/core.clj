(ns tungsten.core
  (:require [tungsten.cli :as cli]
            [tungsten.configuration :as config])
  (:gen-class))

(defn -main
  [& args]
  (-> (cli/parse args)
      (get-in [:options :config])
      (config/read-configuration)
      (.start)))