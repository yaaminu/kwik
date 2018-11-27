(ns kwik.protocol
  (:require [clojure.string :refer [split]]))

(defn handle-request [req]
  (let [command (-> req :params :command)
        key (-> req :params :key)
        ; splitting "," with ',' as delimiter will produce an empty array which is what we want
        ; so we specify that as the default when there's no query params named arg.
        args (split (or (-> req :params :args) ",") #",")]
    {:name command :key key :args args}))


(defn render-response [kwik-command-response]
  (let [[res err] kwik-command-response]
    (if (nil? err)
      {:status 200 :body (str res "\r\n")}
      {:status 400 :body (str err "\r\n")})))