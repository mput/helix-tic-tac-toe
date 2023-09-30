(ns dev
  (:require
   [helix.experimental.refresh :as r]))

(r/inject-hook!)

(defn ^:dev/after-load ^:export refresh []
  (r/refresh!))
