(ns tungsten.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.cli :as tool-cli])
  (:import [java.io File]))

(def cli-options
  [["-c" "--config <path-to-config>" "Configuration file"
    :default "/etc/tungsten/config.edn"
    :parse-fn #(str %)
    :validate [#(-> (File. ^String %)
                    (.exists)) "Config file not found"]]
   ["-e" "--env <dev/prod>" "Application environment"
    :default "dev"
    :parse-fn #(str %)
    :validate [#(or (= "dev" %)
                    (= "env" %)) "Application environment invalid"]]
   ["-h" "--help" "Shows this menu"]])

(defn usage-menu [cli-summary]
  (str "Tungsten: Instagram bots-as-a-service \n" cli-summary))

(defn parse [args]
  (let [parsed-opts (tool-cli/parse-opts args cli-options)]
    (when (not-empty (:errors parsed-opts))
      (println (first (:errors parsed-opts)))
      (System/exit 1))
    (if (get-in parsed-opts [:options :help])
      (do (println (usage-menu (:summary parsed-opts)))
          (System/exit 1))
      parsed-opts)))
