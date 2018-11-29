(ns kwik.commands.generic
  (:require [kwik.types :refer [kwik-value]]
            [kwik.errors :refer [ERR_INVALID_PATTERN]])
  (:import (java.util.regex PatternSyntaxException)))


;DEL
(defn kwik-delete [kwik-database key _]
  ; Remove the mapping for {key} from {kwik-database}
  ; returns the standard kwik tuple
  (do
    (swap! kwik-database dissoc key)
    ["OK" nil])
  )

;KEYS
(defn kwik-search-keys [kwik-database key-pattern _]
  ; Retrieves all keys in {kwik-database} that match the regex {key-pattern}
  (let [keys (vec (keys @kwik-database))
        pattern (try (re-pattern key-pattern) (catch PatternSyntaxException _ nil))]
    (if (nil? pattern)
      [nil ERR_INVALID_PATTERN]
      [(vec (filter (fn [key] (re-matches pattern key)) keys)) nil]
      ))
  )