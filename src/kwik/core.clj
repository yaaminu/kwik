(ns kwik.core
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.route :refer [not-found]]
            [compojure.core :refer [defroutes GET POST DELETE]]
            [ring.middleware.defaults :refer :all]
            [kwik.server :refer [run-command]]
            [kwik.protocol :refer [handle-request render-response]])
  (:gen-class))

(defroutes all-routes
           (GET "/:command/:key" []
             (fn [req] (-> req handle-request run-command render-response)))

           (not-found "Not found"))
(defn -main
  [&]
  (run-server (wrap-defaults all-routes site-defaults) {:port 8000 :thread 1}))
