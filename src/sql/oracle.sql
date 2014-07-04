-- these sql queries are not used directly in the clojure codes.

-- list of all tables in specified schemas
select owner, table_name
from all_tables
where owner in (:o1, :o2, ...);

-- list of tables in a specified schema
select table_name
from all_tables
where owner = :o;

-- table comment
select comments
from all_tab_comments
where  owner = :o and table_name = :t;

-- table stats
select num_rows, blocks, avg_row_len, partitioned, last_analyzed
from all_tables
where owner = :o and table_name = :t;

-- column descriptions for a specified table
select a.column_name,
       a.data_type, a.data_length, a.data_precision, a.data_scale,
       a.nullable, a.data_default, a.num_distinct,
       b.comments
from all_tab_columns a
join all_col_comments b
on (a.owner = b.owner and a.table_name = b.table_name and a.column_name = b.column_name)
where a.owner = :o and a.table_name = :t;

-- index stats
select uniqueness, num_rows, distinct_keys
from all_indexes
where owner = :o and index_name = :i;

-- index description
select index_name, column_position, column_name
from all_ind_columns
where table_owner = :o and index_name = :i;
