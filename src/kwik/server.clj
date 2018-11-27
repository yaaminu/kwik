(ns kwik.server
  (:require [kwik.errors :refer :all]
            [kwik.commands.strings :refer :all]
            [clojure.string :refer [upper-case]]))

(def kwik-database (atom {}))

(def command-table
  {"GET" {:mutates false :run kwik-get}
   "SET" {:mutates true :run kwik-set}})

(defn- find-server-command [name]
  (if (contains? command-table name)
    [(get command-table name) nil]
    [nil ERR_COMMAND_NOT_FOUND])
  )


(defn run-command [client-command]
  (let [{command-name :name target-key :key args :args} client-command
        [server-command err] (find-server-command (upper-case command-name))]
    (if (nil? server-command)
      [nil err]
      (do
        ((get server-command :run) kwik-database target-key args)))
    ))

