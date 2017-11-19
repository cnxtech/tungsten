(ns tungsten.logger
  (:require [clj-json.core :as json]
            [taoensso.timbre :as timbre
             :refer [info debug warn fatal error]]
            [clojure.string
             :refer [join ends-with?]]
            [taoensso.timbre.appenders.3rd-party.rolling :as roll]
            [taoensso.timbre.appenders.core :as appenders]))

(def file-name "tungsten.log")
(def rolling-pattern :daily)

(defn translate-level [level]
  (case level
    :warn :minor
    :fatal :major
    :error :critical
    :info :info))

(defn get-path [path]
  (if (ends-with? path "/")
    (join [path file-name])
    (join "/" [path file-name])))

(defn log-output-fn [{:keys [timestamp_ level msg_] :as args}]
  (json/generate-string {:timestamp @timestamp_
                         :level (translate-level level)
                         :message msg_}))

(defn log-file-mode
  ([] (timbre/merge-config!
        {:appenders {:println {:enabled? true
                               :output-fn log-output-fn}}}))
  ([log-file-path] (timbre/merge-config!
                     {:appenders {:println {:enabled? true
                                            :output-fn log-output-fn}
                                  :rolling
                                  (merge (roll/rolling-appender
                                           {:path (get-path log-file-path)
                                            :pattern rolling-pattern})
                                         {:output-fn log-output-fn})}})))

(defn set-log-configuration [log-configuration]
  (case (:log-mode log-configuration)
    :std-out (log-file-mode)
    :file (log-file-mode (:log-file-path log-configuration))))

(defn log
  ([message] (log :info message))
  ([level message]
   (case level
     :minor (warn message)
     :major (fatal message)
     :critical (error message)
     :info (info message))))