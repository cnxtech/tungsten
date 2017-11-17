(ns tungsten.logger
  (:require [clj-json.core :as json]
            [taoensso.timbre :as timbre
             :refer [info debug warn fatal error]]
            [taoensso.timbre.appenders.core :as appenders]))

(defn translate-level [level]
  (case level
    :warn :minor
    :fatal :major
    :error :critical))

(defn log-output-fn [{:keys [timestamp_ level msg_] :as args}]
  (json/generate-string {:timestamp @timestamp_
                         :level (translate-level level)
                         :message msg_}))

(defn std-out-mode []
  (timbre/merge-config!
    {:appenders {:println {:output-fn log-output-fn}}}))

(defn log-file-mode
  ([] (timbre/merge-config!
        {:appenders {:spit (merge (appenders/spit-appender {:fname "timbre.log"})
                                  {:output-fn log-output-fn})
                     :println {:enabled? false}}}))
  ([log-file-path] (timbre/merge-config!
                     {:appenders {:spit
                                  (merge (appenders/spit-appender
                                           {:fname log-file-path})
                                         {:output-fn log-output-fn})
                                  :println {:enabled? false}}})))

(defn set-log-configuration [log-configuration]
  (case (:log-mode log-configuration)
    :std-out (std-out-mode)
    :log-file (log-file-mode)))

(defn log
  ([message] (info message))
  ([level message]
   (case level
     :minor (warn message)
     :major (fatal message)
     :critical (error message))))

(defn set-default-config []
  (log-file-mode))