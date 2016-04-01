(ns alzatech.data-exp-stack-exchange.queries
  (:require [alzatech.data-exp-stack-exchange.sparkling.sql :as sql])
  (:import [org.apache.spark.sql Column]))

(defn into-string-array
  "Helper function for java interop"
  [& strings]
  (into-array String strings))

(defn into-column-array
  "Helper function for java interop"
  [& columns]
  (into-array Column columns))

(defn read-data-into-df
  "Read in parquet data from dir and return it as a Spark dataframe"
  [sql-ctx parquet-dir]
  (sql/parquet-file sql-ctx (into-string-array parquet-dir))
  )

(defn query-users-top-ten-with-reputation-over-1000
  [users-df]
  (-> users-df
    (.select (into-column-array (.col users-df "name") (.col users-df "reputation")))
    (.filter "reputation > 1000")
    (.orderBy (into-column-array (-> users-df
                                   (.col "reputation")
                                   (.desc))))
    (.limit 10)
    (.show)))

