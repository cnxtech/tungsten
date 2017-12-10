(ns tungsten.logger
  (:require [clj-json.core :as json]
            [taoensso.timbre :as timbre
             :refer [info debug warn fatal error debug]]
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
    :info :info
    :debug :debug))

(defn get-path [path]
  (if (ends-with? path "/")
    (join [path file-name])
    (join "/" [path file-name])))

(defn logger-fn [{:keys [timestamp_ level msg_] :as args}]
  (json/generate-string {:timestamp @timestamp_
                         :level (translate-level level)
                         :message msg_}))

(defn log-file-mode
  ([level]
   (timbre/merge-config!
     {:level level
         :appenders {:println {:enabled? true
                               :output-fn logger-fn}}}))
  ([log-file-path level] (timbre/merge-config!
                           {:level level
                      :appenders {:println {:enabled? true
                                            :output-fn logger-fn}
                                  :rolling
                                  (merge (roll/rolling-appender
                                           {:path (get-path log-file-path)
                                            :pattern rolling-pattern})
                                         {:output-fn logger-fn})}})))

(defn set-log-configuration [log-configuration]
  (case (:mode log-configuration)
    :std-out (log-file-mode (:level log-configuration))
    :file (log-file-mode (:file-path log-configuration)
                         (:level log-configuration))))

(defn log
  ([message] (log :info message))
  ([level message]
   (case level
     :minor (warn message)
     :major (fatal message)
     :critical (error message)
     :info (info message)
     :debug (debug message))))