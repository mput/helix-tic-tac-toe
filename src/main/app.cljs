(ns app
  (:require [helix.core :refer [<> $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            [lib :refer [defnc] :include-macros true]
            ["react" :as react]
            ["react-dom/client" :as rdom]))


(defnc square [{:keys [value on-square-clicke]}]
  (d/button
   {:class "square"
    :on-click on-square-clicke}
   value))

(defn calculate-winner [squares]
  (some (fn [vinner-keys]
          (let [marks (->> vinner-keys
                           (map squares vinner-keys)
                           set)]
            (when (= 1 (count marks))
              (first marks))))
        [[0 1 2]
         [3 4 5]
         [6 7 8]
         [0 3 6]
         [1 4 7]
         [2 5 8]
         [0 4 8]
         [2 4 6]]))

(defnc board [{:keys [squares x-next? on-play]}]
  (let [winner (calculate-winner squares)
        next-mark (if x-next? "X" "O")
        set-square (fn [r]
                     (when-not (or (get squares r) winner)
                       (on-play (assoc squares r next-mark))))
        status (if winner
                 (str "Winner: " winner)
                 (str "Next player: " next-mark))]
    (<>
     (d/div {:class "status"} status)
     (for [r (range 3)]
       (d/div {:class "board-row"
               :key r}
              (for [c (range 3)
                    :let [idx (+ (* r 3) c)]]
                (square {:key idx
                         :value (get squares idx)
                         :on-square-clicke #(set-square idx)}))))) ))

(defnc game []
  (let [[history set-history] (hooks/use-state [{}])
        [current-move set-current-move] (hooks/use-state 0)

        x-next? (even? current-move)

        current-squares (get history current-move)

        handle-play (fn [new-squares]
                      (set-current-move inc)
                      (set-history (conj (vec (take (inc current-move) history))
                                         new-squares)))

        jump-to (fn [next-move]
                  (set-current-move next-move))

        moves (for [[idx _] (map-indexed vector history)
                    :let [label (if  (zero? idx)
                                  "Go to game start"
                                  (str "Go to move #" idx))]]
                (d/li {:key idx :on-click #(jump-to idx)}
                      (d/button
                       (d/span {:class (when (> idx current-move)
                                         "strikethrough")}
                               label))))]
    (d/div {:class "game"}
           (d/div {:class "game-board"}
                  (board {:squares current-squares
                          :x-next? x-next?
                          :on-play handle-play}))
           (d/div {:class "game-info"}
                  (d/ol moves)))))

(defnc app []
  (game))

(defonce root (rdom/createRoot (js/document.getElementById "root")))

(defn ^:export  init []
  (.render root ($ react/StrictMode (app))))
