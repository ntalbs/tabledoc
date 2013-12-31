(ns tabledoc
  (:require [net.cgrand.enlive-html :as h])
  (:use [dbms.oracle]
        [clojure.java.io]
        [clojure.contrib.command-line]))

(h/deftemplate overview-template "templates/overview.html" []
  [:#generation-ts] (h/content (str (java.util.Date.))))

(h/deftemplate schema-list-template "templates/schemas.html" [owners]
  {[[:a (h/nth-of-type 2)]] [:br]} (h/clone-for [o owners]
                                                [:a] (h/do-> (h/set-attr :href (str o "/tables.html"))
                                                             (h/content o))))

(h/deftemplate all-tab-list-template "templates/tab_list.html" [tables]
  {[:a] [:br]} (h/clone-for [t tables]
                            [:a] (h/do-> (h/set-attr :href (str (t :owner) "/" (t :table_name) ".html"))
                                         (h/content (t :table_name)))))

(h/deftemplate tab-list-template "templates/tab_list.html" [owner tables]
  [:h3] (h/content owner)
  {[:a] [:br]} (h/clone-for [t tables]
                            [:a] (h/do-> (h/set-attr :href (str t ".html"))
                                         (h/content t))))

(h/deftemplate tbl-template "templates/tab.html" [tab-info]
  [:h1] (h/content (str (tab-info :owner) "." (tab-info :tab-name)))
  [:.table-desc] (h/content (tab-info :tab-desc))
  [:#v-rows] (h/content (str (get-in tab-info [:tab-stat :num_rows])))
  [:#v-bytes] (let [blocks (get-in tab-info [:tab-stat :blocks])]
                (h/content (if blocks (str (* 8192 blocks)) "")))
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
                                 [:.distinct] (h/content (str (r :distinct_keys)))
                                 [:.rows] (h/content (str (r :num_rows)))))

(defn get-tab-info [owner tab-name]
  {:owner owner
   :tab-name tab-name
   :tab-desc (get-tab-desc owner tab-name)
   :tab-stat (get-tab-stat owner tab-name)
   :col-desc (get-col-desc owner tab-name)
   :idx-desc (get-idx-desc owner tab-name)})

(defn gen-overview [dest]
  (with-open [w (writer (str dest "/overview.html"))]
    (.write w (apply str (overview-template)))))

(defn gen-schema-list [dest owners]
  (with-open [w (writer (str dest "/schemas.html"))]
    (.write w (apply str (schema-list-template owners)))))

(defn gen-all-tab-list [dest owners]
  (with-open [w (writer (str dest "/all_tables.html"))]
    (.write w (apply str (all-tab-list-template (get-all-tables owners))))))

(defn gen-tab-list [dest owners]
  (doseq [owner owners]
    (let [path (str dest "/" owner "/tables.html")]
      (make-parents path)
      (with-open [w (writer path)]
        (.write w (apply str (tab-list-template owner (get-tables owner))))))))

(defn gen-tab-files [dest owner]
  (doseq [tab-name (get-tables owner)]
    (println (str dest "/" owner "/" tab-name ".html"))
    (with-open [w (writer (str dest "/" owner "/" tab-name ".html"))]
      (.write w (apply str (tbl-template (get-tab-info owner tab-name)))))))

(defn copy-file [src dest]
  (do
    (make-parents dest)
    (copy (file src) (file dest))))

(defn -main [& args]
  (with-command-line args
    "tabledoc:"
    [[d "destination directory" "./db_doc"]
     [s "schemas (or databases or owners): comma separated list"]
     remaining]
    (let [dest d owners (clojure.string/split s #",")]
      (copy-file "src/templates/index.html" (str dest "/index.html"))
      (copy-file "src/templates/stylesheet.css" (str dest "/stylesheet.css"))
      (gen-overview dest)
      (gen-schema-list dest owners)
      (gen-all-tab-list dest owners)
      (gen-tab-list dest owners)
      (doseq [owner owners]
        (gen-tab-files dest owner)))))
