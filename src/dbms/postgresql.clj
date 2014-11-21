(ns dbms.postgresql
  (:use (korma db core config))
  (:require (clojure [string :as str])))

(defdb pgdb
  (postgres {:host "localhost"
             :port "5432"
             :db "postgres"
             :user "xxx"
             :password "xxx"}))

(defentity pg_tables)

(defn list-t1 []
  (select pg_tables
          (fields :tablename)))
