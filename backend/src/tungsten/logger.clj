(ns tungsten.logger
  (:require [taoensso.timbre :as timbre
             :refer [info debug warn fatal error debug]]
            [taoensso.timbre.appenders.3rd-party.rolling :as roll]
            [clojure.string :as str])
  (:import [java.time Instant]
           [java.util Date]
           (java.text SimpleDateFormat)))

;; TODO Custom log file name and rolling pattern
(def file-name "tungsten.log")
(def rolling-pattern :daily)

(defn get-path [path]
  (if (str/ends-with? path "/")
    (str/join [path file-name])
    (str/join "/" [path file-name])))

(defn translate-level [level]
  (case level
    :warn :minor
    :fatal :major
    :error :critical
    :info :info
    :debug :debug))

(defn instant->date-time [instant-obj]
  "Takes an instant object, formats to DD-MM-YYYY:HH:MM:SS"
  (let [formatter (SimpleDateFormat. "dd-MM-yyyy:HH:mm:ss")]
    (->> (Date/from instant-obj)
       (.format formatter))))

(defn logger-output-fn
  ([data] (logger-output-fn nil data))
  ([opts data]
   (let [{:keys [level ?err msg_ ?ns-str hostname_]} data]
     (str
       (format
         "[%s] %s %s:%s - %s"
         (str/upper-case (name (translate-level level)))
         (instant->date-time (Instant/now))
         (force hostname_)
         (force ?ns-str)
         (force msg_))
       (when-let [err ?err]
         (str "\n" (timbre/stacktrace err opts)))))))

(defn log-file-mode
  ([level]
   (timbre/merge-config!
     {:level level
         :appenders {:println {:enabled? true
                               :output-fn logger-output-fn}}}))
  ([log-file-path level] (timbre/merge-config!
                           {:level level
                      :appenders {:println {:enabled? true
                                            :output-fn logger-output-fn}
                                  :rolling
                                  (merge (roll/rolling-appender
                                           {:path (get-path log-file-path)
                                            :pattern rolling-pattern})
                                         {:output-fn logger-output-fn})}})))

(defn set-log-config [log-configuration]
  (case (:mode log-configuration)
    :std-out (log-file-mode (:level log-configuration))
    :file (log-file-mode (:file-path log-configuration)
                         (:level log-configuration))))