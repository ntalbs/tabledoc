(defproject tabledoc "0.1.0"
  :description "Oracle Schema documentation generator."
  :license {:name "The MIT License (MIT)" :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojure-contrib "1.2.0"] 
                 [org.clojure/java.jdbc "0.3.3"]
                 [local/ojdbc6 "11.2.0.1"]
                 [mysql/mysql-connector-java "5.1.30"]
                 [org.postgresql/postgresql "9.3-1101-jdbc41"]
                 [enlive "1.1.5"]
                 [korma "0.3.1"]]
  :main tabledoc
  :plugins [[cider/cider-nrepl "0.6.0"]]
)
