(ns kwik.server
  (:require [kwik.errors :refer :all]
            [kwik.commands.strings :refer :all]
            [kwik.commands.generic :refer :all]
            [kwik.commands.lists :refer :all]
            [kwik.commands.maps :refer :all]
            [clojure.string :refer [upper-case]]))

(def __kwik-database (atom {}))

(defn- echo [_ arg1 _]
  [(str arg1), nil])

(def command-table
  ; represents a map of the commands supported by kwik
  ; Each commands has:
  ;
  ; mutates: specifies whether or not the commands changes the database
  ;
  ; run: (fn [kwik-database:map arg1:string args:vector] ;body )-> [results err]
  ;      The function returns a tuple in the form of [results err]. At any point in
  ;      time, only one is not nil. the second value in the tuple indicates an error
  ;
  ; arity: No of arguments the commands expects including the key i.e (count args) + 1
  ;      negative means 'at least'. Currently arity is not checked and it's only for
  ;      for documentation purposes.
  ;
  {"ECHO"    {:mutates false :run echo :arity 1}
   "GET"     {:mutates false :run kwik-get :arity 1}
   "SET"     {:mutates true :run kwik-set :arity 2}
   "DEL"     {:mutates true :run kwik-delete :arity 1}
   "LGET"    {:mutates false :run kwik-lget :arity 1}
   "LSET"    {:mutates true :run kwik-lset :arity -2}
   "LAPPEND" {:mutates true :run kwik-lappend :arity -2}
   "LPOP"    {:mutates true :run kwik-lpop :arity 1}
   "MSET"    {:mutates true :run kwik-mset :arity -2}
   "MGET"    {:mutates false :run kwik-mget :arity 2}
   "MGETALL" {:mutates true :run kwik-mgetall :arity 1}
   "MDEL"    {:mutates false :run kwik-mdel :arity 2}
   "KEYS"    {:mutates false :run kwik-search-keys :arity 1}
   })



(defn- find-server-command [name]
  (get command-table (upper-case name) nil))



(defn- check-arity [command client-arg-count]

  (let [{arity :arity} command]

    ; convert negative arity to positive and check if client commands is >= arity
    ; see the description of arity above
    (cond
      (< arity 0) (>= client-arg-count (* -1 arity))
      :else (= arity client-arg-count)
      )))


(defn run-command [client-command]

  ; given a particular commands retrieve it from the commands table in a case
  ; insensitive manner and pass it the client supplied arguments.
  ; this routine returns a tuple with first value indicating the results and
  ; the last one indicating err. At any point in time, only one is not nil

  (let [{command-name :name target-key :key args :args} client-command
        server-command (find-server-command command-name)]

    (if (nil? server-command)

      [nil ERR_COMMAND_NOT_FOUND]
      ; +1 for the first argument. See docs above
      (let [arity-ok (check-arity server-command (+ (count args) 1))]

        (if (true? arity-ok)

          ((get server-command :run) __kwik-database target-key args)
          [nil ERR_WRONG_ARITY]
          ))
      )))