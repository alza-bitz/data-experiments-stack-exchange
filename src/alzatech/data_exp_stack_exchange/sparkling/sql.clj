(ns alzatech.data-exp-stack-exchange.sparkling.sql 
  (:import [org.apache.spark.sql SQLContext Row]))

;; ## EXPERIMENTAL
;;
;; This code as well as SparkSQL itself are considered experimental.
;;

;; ## JavaSQLContext
;;
(defn sql-context [spark-context]
  (SQLContext. spark-context))

(defn sql [sql-context query]
  (.sql sql-context query))

(defn parquet-file [sql-context path]
  (.parquetFile sql-context path))

(defn json-file [sql-context path]
  (.jsonFile sql-context path))

(defn register-rdd-as-table [sql-context rdd table-name]
  (.registerRDDAsTable sql-context rdd table-name))

(defn cache-table [sql-context table-name]
  (let [scala-sql-context (.sqlContext sql-context)]
    (.cacheTable scala-sql-context table-name)))

;; ## JavaSchemaRDD
;;
(defn register-as-table [rdd table-name]
  (.registerAsTable rdd table-name))

;; DataFrame
(defn register-temp-table
  "Registers this dataframe as a temporary table using the given name."
  [df table-name]
  (.registerTempTable df table-name))

(def print-schema (memfn printSchema))

;; ## Row
;;
(fn row->vec [^Row row]
  (let [n (.length row)]
    (loop [i 0 v (transient [])]
      (if (< i n)
        (recur (inc i) (conj! v (.get row i)))
        (persistent! v)))))