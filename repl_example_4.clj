; RESTART REPL

(use 'clojure.repl)

(require '[alzatech.data-exp-stack-exchange.sparkling.context :as ctx] )

; make a spark context

(def ctx (ctx/spark-context-for-app-named "Stack Exchange Queries"))

(require '[alzatech.data-exp-stack-exchange.sparkling.sql :as sql])

; make a spark sql context

(def sql-ctx (sql/sql-context ctx))

(use 'alzatech.data-exp-stack-exchange.queries)

; read in parquet data for users

(def users-df (read-data-into-df sql-ctx "data/cleansed/users"))

; execute a query using spark dataframe api - who are the top 10 users by reputation?

(query-users-top-ten-with-reputation-over-1000 users-df)

; read in parquet data for posts

(def posts-df (read-data-into-df sql-ctx "data/cleansed/posts"))

; register some temp tables

(sql/register-temp-table users-df "users")
(sql/register-temp-table posts-df "posts")

; execute a query using spark sql - who are the top 10 users by number of posts about comets?

(.show (sql/sql sql-ctx "select u.name, count(1) as number_of_posts 
   from users u, posts p 
   where p.tags like '%comets%' 
   and u.id = p.ownerId
   group by u.name order by number_of_posts desc limit 10"))