(ns app
  (:require [helix.core :refer [$]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            [lib :refer [defnc] :include-macros true]
            ["react-dom/client" :as rdom]))

(defnc greeting
  [{:keys [name]}]
  (d/div "Hello: "
         (d/strong name)))

(defnc app []
  (let [[state set-state] (hooks/use-state {:name "?"})]
    (d/div
     (d/h1 "Welcome")
     ($ greeting {:name (:name state)})
     (d/input {:on-change #(set-state assoc :name (.. % -target -value))}))))

(defonce root (rdom/createRoot (js/document.getElementById "root")))

(defn ^:export  init []
  (.render root ($ app)))
