(ns kwik.commands.maps
  (:require [kwik.errors :refer :all]
            [kwik.types :refer [kwik-value]]
            [kwik.commands.generic :refer [kwik-delete]]))


(defn- _do-update-map [kwik-database key target-map new-map]
  (let [new-value (struct-map kwik-value :type "map" :value (merge target-map new-map))]
    (swap! kwik-database assoc key new-value)
    ["OK" nil])
  )

(defn- transform-args-to-map [arg]
  ; transform a list of space separated values into key-val pairs and returns them as a map
  (cond
    (< (count arg) 1) [nil ERR_WRONG_ARITY]
    ;To check that the arguments are of correct structure, each entry must be in the form (key val)
    ;We achieve this by checking for at leas one entry that's not space separated
    (true? (some #(= 1 (count (clojure.string/split % #"\s" 2))) arg)) [nil ERR_MALFORMED_ARGUMENTS]
    :else [(into {} (map #(clojure.string/split % #"\s" 2) arg)) nil])
  )



;MGETALL
(defn kwik-mgetall [kwik-database key _]
  ;Retrieves and returns the all entries of the map stored
  ; at {key} in {kwik-database}
  (let [{kwik-db-entry key} @kwik-database]
    (if (nil? kwik-db-entry)
      [nil ERR_MAPPING_NOT_FOUND]
      (let [{type :type value :value} kwik-db-entry]
        (if (= type "map")
          [value nil]
          [nil ERR_TYPE_MISMATCH]))
      ))
  )

;MGET
(defn kwik-mget [kwik-database key argV]
  ; Retrieves the map entry stored at (first argV) of the map point to by {key}
  ; in the {kwik-database}, {argV} is a vector containing exactly one value which
  ; will serve as the key for the entry being retrieved
  (if (not= 1 (count argV))
    [nil ERR_WRONG_ARITY]
    (let [{kwik-db-entry key} @kwik-database
          [map-key] argV]
      (if (nil? kwik-db-entry)
        [nil ERR_MAPPING_NOT_FOUND]
        (let [{type :type value :value} kwik-db-entry]
          (if (= type "map")
            [(get value map-key "") nil]
            [nil ERR_TYPE_MISMATCH]))
        )))
  )

;MSET
(defn kwik-mset [kwik-database key argV]
  ;Adds a new map entry to the map stored at {key} in the {kwik-database}
  ;{argV} contains at least 1 space separated value in the form "key value".
  ;Each value is transformed to a key-value pair and then added to the corresponding map.
  ;In case a value does not contain a space, this command will fail with ERR_MALFORMED_ARGUMENTS
  (let [{kwik-db-entry key} @kwik-database
        [new-map err] (transform-args-to-map argV)]
    (if (nil? err)
      (let [{type :type existing-map :value} kwik-db-entry]
        (cond
          (nil? kwik-db-entry) (_do-update-map kwik-database key {} new-map)
          (= type "map") (_do-update-map kwik-database key existing-map new-map)
          :else [nil ERR_TYPE_MISMATCH]))
      [nil err]))
  )

;MDEL
(defn kwik-mdel [kwik-database key argV]
  ; Remove the entry stored at the key (first argV) from the map at {key} in {kwik-database}
  ; This command will not fail even if the specified map-key is not in the map.
  (let [[map-key] argV
        {kwik-db-entry key} @kwik-database
        {type :type value :value} kwik-db-entry]
    (cond
      (nil? kwik-db-entry) ["OK" nil]
      (not= type "map") [nil ERR_TYPE_MISMATCH]
      :else (do
              (swap! kwik-database assoc key (struct-map kwik-value :type "map" :value (dissoc value map-key)))
              ["OK" nil])
      ))
  )