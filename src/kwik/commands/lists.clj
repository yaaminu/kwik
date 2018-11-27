(ns kwik.commands.lists
  (:require [kwik.errors :refer :all]
            [kwik.types :refer [kwik-value]]
            [kwik.commands.generic :refer [kwik-delete]]))




(defn- _do-pop [kwik-database key target-list]
  (cond
    (= 0 (count target-list)) [nil, ERR_LIST_EMPTY]         ;impossible but just in case
    (= 1 (count target-list)) (do
                                (kwik-delete kwik-database key nil)
                                [(peek target-list) nil])
    :else (do
            (swap! kwik-database assoc key (struct-map kwik-value :type "list" :value (pop target-list)))
            [(peek target-list) nil])
    ))

(defn kwik-lget [kwik-database key _]
  (let [{kwik-db-entry key} @kwik-database]
    (if (nil? kwik-db-entry)
      [nil ERR_MAPPING_NOT_FOUND]
      (do
        (let [{type :type value :value} kwik-db-entry]
          (if (= type "list")
            [value nil]
            [nil ERR_TYPE_MISMATCH])))
      )))



(defn kwik-lset [kwik-database key argV]
  ;argV is a vector that contains the new entries
  (if (= 0 (count argV))
    [nil ERR_WRONG_ARITY]
    (do
      (swap! kwik-database assoc key (struct-map kwik-value :type "list" :value argV))
      [(count argV) nil])
    ))


(defn kwik-lappend [kwik-database key argV]
  (if (= 0 (count argV))
    [nil ERR_WRONG_ARITY]
    (let [{kwik-db-entry key} @kwik-database
          {type :type existing-list :value} kwik-db-entry]
      (cond
        (nil? kwik-db-entry) (kwik-lset kwik-database key argV) ;no mapping exists yet, add a new mapping
        (= type "list") (kwik-lset kwik-database key (into existing-list argV)) ;there's an existing mapping with appropriate type
        :else [nil ERR_TYPE_MISMATCH]
        ))))

(defn kwik-lpop [kwik-database key _]
  (let [{kwik-db-entry key} @kwik-database
        {type :type list :value} kwik-db-entry]
    (cond
      (nil? kwik-db-entry) [nil ERR_MAPPING_NOT_FOUND]
      (= type "list") (_do-pop kwik-database key list)
      :else [nil ERR_TYPE_MISMATCH]
      )
    ))

