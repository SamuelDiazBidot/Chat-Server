(ns chat-server.core
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer :all]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [ring.util.response :refer [response file-response]]
            [ring.middleware.json :refer :all]
            [ring.middleware.cors :refer [wrap-cors]]))

(def addMessage-channels (atom #{}))
(def deleteMessage-channels (atom #{}))

(defn add-channel [channel channels]
  (swap! channels conj channel))

(defn remove-channel [channel channels]
  (swap! channels disj channel))

(def id (atom 0))

(defn increment-id [message]
  (let [mapMessage (json/read-str message :key-fn keyword)
        incMessage (assoc mapMessage :uid (swap! id inc))]
    (json/write-str incMessage)))

(defn addMessage-handler [request]
  (with-channel request channel 
    (add-channel channel addMessage-channels)
    (on-close channel (fn [status]
                        (remove-channel channel addMessage-channels)))
    (on-receive channel (fn [data] 
                          (let [message (increment-id data)]
                            (println (str message))
                            (doseq [ch @addMessage-channels]
                              (send! ch message)))))))

(defn deleteMessage-handler [request]
  (with-channel request channel 
    (add-channel channel deleteMessage-channels)
    (on-close channel (fn [status]
                        (remove-channel channel deleteMessage-channels)))
    (on-receive channel (fn [data]
                          (println "Deleting message with uid " data)
                          (doseq [ch @deleteMessage-channels]
                            (send! ch data))))))

(defroutes app-routes
  (GET "/addMessage" [] addMessage-handler)
  (GET "/deleteMessage" [] deleteMessage-handler))

(def chat-app
  (-> app-routes
    (wrap-cors :access-control-allow-origin [#".*"] :access-control-allow-methods [:get])))

(defn -main [] 
  (print "running server at port 5000")
  (run-server chat-app {:port 5000}))
