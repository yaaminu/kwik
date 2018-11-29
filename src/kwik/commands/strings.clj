(ns kwik.commands.strings
  (:require [kwik.errors :refer :all]
            [kwik.types :refer [kwik-value]]))


;GET
(defn kwik-get [kwik-database key _]
  ;Retrieves the string value stored at {key} from {kwik-database}
  (let [{kwik-db-entry key} @kwik-database
        {type :type value :value} kwik-db-entry]
    (if (nil? kwik-db-entry)
      [nil ERR_MAPPING_NOT_FOUND]
      (if (= type "string")
        [value nil]
        [nil ERR_TYPE_MISMATCH])))
  )



(defn kwik-set [kwik-database key argV]
  ;Adds a new mapping at {key} in {kwik-databas}
  ;argV is a vector but it's should contain exactly one entry
  (let [[value] argV]
    (do
      (swap! kwik-database assoc key (struct-map kwik-value :type "string" :value (str value)))
      ["OK" nil])
    ))