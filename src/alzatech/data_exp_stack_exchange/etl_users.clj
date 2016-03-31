(ns alzatech.data-exp-stack-exchange.etl-users
  (:require 
    [alzatech.data-exp-stack-exchange.sparkling.context :as ctx]
    [alzatech.data-exp-stack-exchange.sparkling.sql :as sql]
    [sparkling.core :as sc]
    [sparkling.serialization]
    [clojure.data.xml :as xml])
  (:import [org.apache.spark.sql RowFactory]
           [org.apache.spark.sql.types StructType StructField Metadata DataTypes])
  (:gen-class))

(defn parse-int
  "Reads a string, returns a number or nil if the string was x"
  [x]
  (if (nil? x)
    x
    (Integer/parseInt x)))

(defn xml->row
  "Parse a row of user xml and return it as a Spark Row"
  [user-xml]
  (let [user (xml/parse-str user-xml)
        {{:keys [Id DisplayName Reputation]} :attrs} user]
    [(RowFactory/create (into-array Object [(parse-int Id) DisplayName  (parse-int Reputation)]))]))

"Spark function that reads in a line of XML and potentially returns a Row"
(defn parse-user
  [user-xml]
  (if (.startsWith user-xml  "  <row")
    (xml->row user-xml)
    []))

(def user-schema
  (StructType.
   (into-array StructField [(StructField. "id" (DataTypes/IntegerType) true (Metadata/empty))
                            (StructField. "name" (DataTypes/StringType) true (Metadata/empty))
                            (StructField. "reputation" (DataTypes/IntegerType) true (Metadata/empty))])))

(defn -main [& args]
  (let [input-path (first args)
        output-path (second args)
        ctx (ctx/spark-context-for-app-named "Stack Exchange ETL Users")
        sql-ctx (sql/sql-context ctx)
        xml-users (sc/text-file ctx input-path)
        users (sc/flat-map parse-user xml-users)
        users-df (.createDataFrame sql-ctx users user-schema)]
    (.saveAsParquetFile users-df output-path)))
