(ns kwik.commands.generic
  (:require [kwik.types :refer [kwik-value]]
            [kwik.errors :refer [ERR_INVALID_PATTERN]])
  (:import (java.util.regex PatternSyntaxException)))



(defn kwik-delete [kwik-database key _]
  (do
    (swap! kwik-database dissoc key)
    ["OK" nil])
  )


(defn kwik-search-keys [kwik-database key-pattern _]
  (let [keys (vec (keys @kwik-database))
        pattern (try (re-pattern key-pattern) (catch PatternSyntaxException _ nil))]
    (if (nil? pattern)
      [nil ERR_INVALID_PATTERN]
      [(vec (filter (fn [key] (re-matches pattern key)) keys)) nil]
      ))
  )