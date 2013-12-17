(use '[korma db core config])
(require '[clojure.string :as str])

(def db-spec
  {:classname "oracle.jdbc.OracleDriver"
   :subprotocol "oracle"
   :subname "thin:@192.168.1.83:1521:testdb"
   :user "xxx"
   :password "xxx"
   :naming {:keys str/lower-case :fields str/upper-case}})

;; (def db-spec
;;   (oracle {:keys ["192.168.1.83" 1521 true]
;;            :as {:user "hr" :password "hr"}
;;            :naming {:keys str/lower-case :fields str/upper-case}}))

(defdb korma-db db-spec)

(defentity all_tables)
(defentity all_tab_columns)
(defentity all_ind_columns)
(defentity all_tab_comments)
(defentity all_col_comments)

(defn get-tables [owners]
  (select all_tables
          (fields :owner :table_name)
          (where {:owner [in owners]})
          (order :table_name)))

(defn get-tab-stat [owner tab-name]
  (select all_tables
          (fields :num_rows :blocks :avg_row_len :partitioned :last_analyzed)
          (where {:owner owner :table_name tab-name})))

(defn get-col-desc [owner tab-name]
  (select all_tab_columns
          (fields :column_name
                  :data_type :data_length :data_precision :data_scale
                  :nullable :data_default :num_distinct
                  :all_col_comments.comments)
          (join all_col_comments
                (and (= :all_col_comments.owner :owner)
                     (= :all_col_comments.table_name :table_name)
                     (= :all_col_comments.column_name :column_name)))
          (where {:owner owner :table_name tab-name})
          (order :column_id)))

(defn get-idx-desc [owner tab-name]
  (->> (select all_ind_columns
               (fields :index_name :column_position :column_name)
               (where {:table_owner owner :table_name tab-name}))
       (group-by #(% :index_name))))
