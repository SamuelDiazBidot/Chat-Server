(defproject chat-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.6.1"]
                 [http-kit "2.3.0"]
                 [ring/ring-json "0.5.0"]
                 [org.clojure/data.json "0.2.6"]
                 [ring-middleware-format "0.7.4"]
                 [ring-cors "0.1.13"]]
  :main chat-server.core)
