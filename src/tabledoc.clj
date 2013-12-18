(ns tabledoc
  (:use (dbms [oracle]))
  (:require (net.cgrand [enlive-html :as h])))

(h/deftemplate tbl-template "templates/tbl.html" [tab-info]
  [:h1] (h/content (str (tab-info :owner) "." (tab-info :tab-name)))
  [:.table-desc] (h/content (tab-info :tab-desc))
  [:#v-rows] (h/content (str (get-in tab-info [:tab-stat :num_rows])))
  [:#v-bytes] (h/content (str (* 8192 (get-in tab-info [:tab-stat :blocks]))))
  [:#v-avg-row-len] (h/content (str (get-in tab-info [:tab-stat :avg_row_len])))
  [:#v-partitioned] (h/content (get-in tab-info [:tab-stat :partitioned]))
  [:#v-last-analyzed] (h/content (str (get-in tab-info [:tab-stat :last_analyzed])))
  [:#col-desc-title] (h/content "Column Description")
  [:tr.per-col-row] (h/clone-for [r (tab-info :col-desc)]
                                 [:.column-name] (h/content (r :column_name))
                                 [:.nullable] (h/content (r :nullable))
                                 [:.data-type] (h/content (r :data_type))
                                 [:.default] (h/content (r :default))
                                 [:.distinct] (h/content (r :distinct))
                                 [:.comment] (h/content (r :comments)))
  [:#index-desc-title] (h/content "Index Description")
  [:tr.per-idx-row] (h/clone-for [r (tab-info :idx-desc)]
                                 [:.index-name] (h/content (r :index_name))
                                 [:.columns] (h/content (r :index_cols))
                                 [:.uniq] (h/content (r :uniqueness))
                                 [:.distinct] (h/content (r :distinct_keys))
                                 [:.rows] (h/content (r :num_rows))))

(defn get-tab-info [owner tab-name]
  {:owner owner
   :tab-name tab-name
   :tab-desc (get-tab-desc owner tab-name)
   :tab-stat (get-tab-stat owner tab-name)
   :col-desc (get-col-desc owner tab-name)
   :idx-desc (get-idx-desc owner tab-name)})

(print
 (apply str (tbl-template (get-tab-info "HR" "EMPLOYEES"))))
