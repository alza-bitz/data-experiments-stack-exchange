(ns alzatech.data-exp-stack-exchange.sparkling.context
  (:require [sparkling.conf :as conf]
            [sparkling.core :as s]))

(defn spark-context-for-app-named
  "Returns a spark context"
  [app-name]
  (let [c (-> (conf/spark-conf)
            (conf/master "local[*]")
            (conf/app-name app-name))]
    (s/spark-context c)))