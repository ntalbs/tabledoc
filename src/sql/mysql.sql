select table_comment
from information_schema.tables
where table_schema = :s
and table_name = :t1
