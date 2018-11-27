(ns kwik.commands.generic
  (:require [kwik.types :refer [kwik-value]]
            [kwik.errors :refer [ERR_WRONG_ARITY]]))



(defn kwik-delete [kwik-database key _]
  (do
    (swap! kwik-database dissoc key)
    ["OK" nil])
  )


(defn kwik-search-keys [kwik-database key-pattern _]
  (let [keys (vec (keys @kwik-database))
        pattern (re-pattern key-pattern)]
    [(vec (filter (fn [key] (re-matches pattern key)) keys)) nil]
    ))