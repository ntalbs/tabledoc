(ns config)

(def db-conf
  {:classname "oracle.jdbc.OracleDriver"
   :subprotocol "oracle"
   :subname "thin:@192.168.1.83:1521:testdb"
   :user "xxx"
   :password "xxx"
   :naming {:key str/lower-case :field str/upper-case})

