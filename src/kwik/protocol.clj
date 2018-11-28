(ns kwik.protocol
  (:require [clojure.string :refer [split]]))

(defn handle-request [req]
  ; Handles requests from clients and parses the parameters
  ; from the request into a commands and returns it.
  ; Most commands
  (let [command (-> req :params :command)
        key (-> req :params :key)
        ; splitting "," with ',' as delimiter will produce an empty array which is what we want
        ; so we specify that as the default when there's no query params named args.
        args (split (or (-> req :params :args) ",") #",")]
    {:name command :key key :args args}))


(defn render-response [kwik-command-response]
  (let [[res err] kwik-command-response]
    (if (nil? err)
      {:status 200 :body (str res)}
      {:status 400 :body (str err)})))