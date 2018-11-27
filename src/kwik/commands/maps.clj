(ns kwik.commands.maps
  (:require [kwik.errors :refer :all]
            [kwik.types :refer [kwik-value]]
            [kwik.commands.generic :refer [kwik-delete]]))

(defn kwik-mget [kwik-database key argV]
  (if (not= 1 (count argV))
    [nil ERR_WRONG_ARITY]
    (let [{kwik-db-entry key} @kwik-database
          [map-key] argV]
      (if (nil? kwik-db-entry)
        [nil ERR_MAPPING_NOT_FOUND]
        (let [{type :type value :value} kwik-db-entry]
          (if (= type "map")
            [(get value map-key) nil]
            [nil ERR_TYPE_MISMATCH]))
        )))
  )


(defn- _do-update-map [kwik-database key target-map map-key map-val]
  (let [new-value (struct-map kwik-value :type "map" :value (assoc target-map map-key map-val))]
    (swap! kwik-database assoc key new-value)
    ["OK" nil])
  )

(defn kwik-mset [kwik-database key argV]
  (if (not= 2 (count argV))
    [nil ERR_WRONG_ARITY]
    (let [{kwik-db-entry key} @kwik-database
          [map-key map-val] argV]
      (let [{type :type existing-map :value} kwik-db-entry]
        (cond
          (nil? kwik-db-entry) (_do-update-map kwik-database key {} map-key map-val)
          (= type "map") (_do-update-map kwik-database key existing-map map-key map-val)
          :else [nil ERR_TYPE_MISMATCH]))
      ))
  )
