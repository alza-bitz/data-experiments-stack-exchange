(defproject data-experiments-stack-exchange "0.1.0-SNAPSHOT"
  :description "Various data processing experiments with Apache Spark, using archived Stack Exchange data"
  :url "https://github.com/alzadude/data-experiments-stack-exchange"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:aot :all}
             :uberjar {:aot :all}
             :provided {:dependencies
                        [[org.apache.spark/spark-core_2.10 "1.6.0"]]}}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [gorillalabs/sparkling "1.2.3"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.apache.spark/spark-sql_2.10 "1.6.0"]])
