(ns kwik.commands.lists
  (:require [kwik.errors :refer :all]
            [kwik.types :refer [kwik-value]]
            [kwik.commands.generic :refer [kwik-delete]]))




(defn- _do-pop [kwik-database key target-list]
  (cond
    (= 0 (count target-list)) [nil, ERR_LIST_EMPTY]
    :else (do
            (swap! kwik-database assoc key (struct-map kwik-value :type "list" :value (pop target-list)))
            [(peek target-list) nil])
    ))

;LGET
(defn kwik-lget [kwik-database key _]
  ; Returns all values stored at {key} in {kwik-database}
  (let [{kwik-db-entry key} @kwik-database]
    (if (nil? kwik-db-entry)
      [nil ERR_MAPPING_NOT_FOUND]
      (do
        (let [{type :type value :value} kwik-db-entry]
          (if (= type "list")
            [value nil]
            [nil ERR_TYPE_MISMATCH])))
      )))


;LSET
(defn kwik-lset [kwik-database key argV]
  ;Stores the vector {argV} into {key} in {kwik-database}
  ;argV is a vector that contains the new entries
  (if (= 0 (count argV))
    [nil ERR_WRONG_ARITY]
    (do
      (swap! kwik-database assoc key (struct-map kwik-value :type "list" :value argV))
      [(count argV) nil])
    ))


;LAPPEND
(defn kwik-lappend [kwik-database key argV]
  ; Appends the vector {argV} to the values already stored at {key}
  ; in {kwik-database}. If there's no existing mapping, a new one is created.
  (if (= 0 (count argV))
    [nil ERR_WRONG_ARITY]
    (let [{kwik-db-entry key} @kwik-database
          {type :type existing-list :value} kwik-db-entry]
      (cond
        (nil? kwik-db-entry) (kwik-lset kwik-database key argV) ;no mapping exists yet, add a new mapping
        (= type "list") (kwik-lset kwik-database key (into existing-list argV)) ;there's an existing mapping with appropriate type
        :else [nil ERR_TYPE_MISMATCH]
        ))))

;LPOP
(defn kwik-lpop [kwik-database key _]
  ; Removes and returns the last entry of the list stored at {key}
  ; in {kwik-database}. When there's no mapping, returns ERR_LIST_EMPTY error.
  ; Even if the last entry is popped, the mapping will not be removed
  (let [{kwik-db-entry key} @kwik-database
        {type :type list :value} kwik-db-entry]
    (cond
      (nil? kwik-db-entry) [nil ERR_MAPPING_NOT_FOUND]
      (= type "list") (_do-pop kwik-database key list)
      :else [nil ERR_TYPE_MISMATCH]
      )
    ))

