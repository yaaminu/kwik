(ns kwik.commands.generic
  (:require [kwik.types :refer [kwik-value]]
            [kwik.errors :refer [ERR_WRONG_ARITY]]))



(defn kwik-delete [kwik-database key _]
  (do
    (swap! kwik-database dissoc key)
    ["OK" nil])
  )