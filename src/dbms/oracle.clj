(ns dbms.oracle
  (:use (korma db core config))
  (:require (clojure [string :as str])))

(def db-spec
  (oracle {:subname "@//192.168.1.83:1521/testdb"
           :user "xxx" :password "xxx"
           :naming {:keys str/lower-case :fields str/upper-case}}))

(defdb korma-db db-spec)

(defentity all_tables)
(defentity all_tab_columns)
(defentity all_indexes)
(defentity all_ind_columns)
(defentity all_tab_comments)
(defentity all_col_comments)

(defn get-all-tables [owners]
  (select all_tables
          (fields :owner :table_name)
          (where {:owner [in owners]})))

(defn get-tables [owner]
  (->> (select all_tables
               (fields :table_name)
               (where {:owner owner})
               (order :table_name))
       (map #(% :table_name))))

(defn get-tab-desc [owner tab-name]
  (:comments (first (select all_tab_comments
                            (fields :comments)
                            (where {:owner owner :table_name tab-name})))))

(defn get-tab-stat [owner tab-name]
  (first (select all_tables
                 (fields :num_rows :blocks :avg_row_len :partitioned :last_analyzed)
                 (where {:owner owner :table_name tab-name}))))

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

(defn- get-idx-cols [cols]
  (->> (sort-by :column_position cols)
       (map :column_name)
       (str/join "+")))

(defn get-idx-stat [owner idx-name]
  (first (select all_indexes
                 (fields :uniqueness :num_rows, :distinct_keys)
                 (where {:owner owner :index_name idx-name}))))

(defn get-idx-desc [owner tab-name]
  (->> (select all_ind_columns
               (fields :index_name :column_position :column_name)
               (where {:table_owner owner :table_name tab-name}))
       (group-by #(% :index_name))
       (map (fn [[n v]]
              (let [idx-stat (get-idx-stat owner n)]
                {:index_name n
                 :index_cols (get-idx-cols v)
                 :uniqueness (idx-stat :uniqueness)
                 :num_rows (idx-stat :num_rows)
                 :distinct_keys (idx-stat :distinct_keys)})))))
