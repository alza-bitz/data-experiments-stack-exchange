(ns alzatech.data-exp-stack-exchange.etl-posts
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
  "Parse a row of post xml and return it as a Spark Row"
  [post-xml]
  (let [user (xml/parse-str post-xml)
        {{:keys [OwnerUserId PostTypeId Tags]} :attrs} user]
    [(RowFactory/create (into-array Object [(parse-int OwnerUserId) (parse-int PostTypeId) Tags]))]))

"Spark function that reads in a line of XML and potentially returns a Row"
(defn parse-post
  [post-xml]
  (if (.startsWith post-xml  "  <row")
    (xml->row post-xml)
    []))

(def post-schema
  (StructType.
   (into-array StructField [(StructField. "ownerId" (DataTypes/IntegerType) true (Metadata/empty))
                            (StructField. "postType" (DataTypes/IntegerType) true (Metadata/empty))
                            (StructField. "tags" (DataTypes/StringType) true (Metadata/empty))])))

(defn -main [& args]
  (let [input-path (first args)
        output-path (second args)
        ctx (ctx/spark-context-for-app-named "Stack Exchange ETL Users")
        sql-ctx (sql/sql-context ctx)
        xml-posts (sc/text-file ctx input-path)
        posts (sc/flat-map parse-post xml-posts)
        posts-df (.createDataFrame sql-ctx posts post-schema)]
    (.saveAsParquetFile posts-df output-path)))
