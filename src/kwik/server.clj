(ns kwik.server
  (:require [kwik.errors :refer :all]
            [kwik.commands.strings :refer :all]
            [kwik.commands.generic :refer :all]
            [kwik.commands.lists :refer :all]
            [kwik.commands.maps :refer :all]
            [clojure.string :refer [upper-case]]))

(def __kwik-database (atom {}))

(def command-table
  {"GET"     {:mutates false :run kwik-get}
   "SET"     {:mutates true :run kwik-set}
   "DEL"     {:mutates true :run kwik-delete}
   "LGET"    {:mutates false :run kwik-lget}
   "LSET"    {:mutates true :run kwik-lset}
   "LAPPEND" {:mutates true :run kwik-lappend}
   "LPOP"    {:mutates true :run kwik-lpop}
   "MSET"    {:mutates true :run kwik-mset}
   "MGET"    {:mutates false :run kwik-mget}
   "MDEL"    {:mutates false :run kwik-mdel}
   "KEYS"    {:mutates false :run kwik-search-keys}
   })

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
        ((get server-command :run) __kwik-database target-key args)))
    ))

