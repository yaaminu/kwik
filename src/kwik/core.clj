(ns kwik.core
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.route :refer [not-found]]
            [compojure.core :refer [defroutes GET]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [kwik.server :refer [run-command]]
            [kwik.protocol :refer [handle-request render-response]]
            [kwik.errors :refer [ERR_UNKNOWN]])
  (:gen-class))

(defroutes all-routes
           (GET "/v1/:command/:key" []
             (fn [req]
               (try (-> req handle-request run-command render-response)
                    (catch Exception e
                      (println e)
                      {:status 500 :body (str ERR_UNKNOWN)}))))
           (not-found (str {"message" "Bad request, unsupported protocol"}))
           )
(defn -main
  [& args]
  (let [port (read-string (or (first args) "8000"))]
    (if (number? port)
      (do
        (println "Starting the kwik server....")
        (println "kwik version: vX.x.x")
        (run-server (wrap-defaults all-routes site-defaults) {:port port :thread 1})
        (println "listening for connections on port " port))
      (println "Port must be a number"))
    )
  )
