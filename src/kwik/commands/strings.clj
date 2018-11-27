(ns kwik.commands.strings
  (:require [kwik.errors :refer :all]
            [kwik.types :refer [kwik-value]]))



(defn kwik-get [kwik-database key _]
  (let [{kwik-db-entry key} @kwik-database]
    (if (nil? kwik-db-entry)
      [nil ERR_MAPPING_NOT_FOUND]
      (do
        (let [{type :type value :value} kwik-db-entry]
          (if (= type "string")
            [value nil]
            [nil ERR_TYPE_MISMATCH])))
      )))



(defn kwik-set [kwik-database key argV]
  (let [[value] argV]
    (if (nil? value)
      [nil ERR_WRONG_ARITY]
      (do
        (swap! kwik-database assoc key (struct-map kwik-value
                                         :type "string"
                                         :value value))
        ["OK" nil])
      )))