(ns dbms.mysql
  (:use (korma db core config))
  (:require (clojure [string :as str])))

(defdb mysqldb
  (mysql {:host "14.63.169.51"
          :port "3306"
          :db "jira"
          :user "jira"
          :password "dprtmaos"}))

(defentity project (database mysqldb))

(defn projects []
  (->>  (select project (fields :pname))
        (map :pname)))

;(projects)

(defentity tables
  (table :information_schema.tables))

(defn get-tables [schema]
  (->> (select information_schema.tables
               (fields :table_name)
               (where {:table_schema schema})
               (order :table_name))
       (map #(% :table_name))))

(get-tables "jira")


(select tables
        (fields :table_name)
        (limit 10))
