(ns chat-server.core
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer :all]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [ring.util.response :refer [response file-response]]
            [ring.middleware.json :refer :all]
            [ring.middleware.cors :refer [wrap-cors]]))

(def channels (atom #{}))

(defn add-channel [channel]
  (swap! channels conj channel))

(defn remove-channel [channel]
  (swap! channels disj channel))

(defn addMessage-handler [request]
  (with-channel request channel 
    (add-channel channel)
    (on-close channel (fn [status]
                        (remove-channel channel)))
    (on-receive channel (fn [data] 
                          (println (str data))
                          (doseq [ch @channels]
                            (send! ch (json/write-str data)))))))

(defroutes app-routes
  (GET "/addMessage" [] addMessage-handler))

(def chat-app
  (-> app-routes
    (wrap-cors :access-control-allow-origin [#".*"] :access-control-allow-methods [:get])))

(defn -main [] 
  (print "running server at port 5000")
  (run-server chat-app {:port 5000}))
