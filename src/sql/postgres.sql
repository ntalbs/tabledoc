-- these sql queries are not used directly in the clojure codes;

-- list of all tables in specified schemas
select schemaname, tablename
from pg_tables
where schemaname in (:s1, :s2, ...);

-- list of all tables in a specified schema
select tablename
from pg_tables
where schemaname = :s;

-- table comment
select description
from   pg_description
where  objoid = 'schema.tanblename'::regclass;

-- table stats


-- column description for a specified table
select distinct
    a.attnum as num,
    a.attname as name,
    format_type(a.atttypid, a.atttypmod) as typ,
    a.attnotnull as notnull,
    c.description as comment
from pg_attribute a
join pg_class pgc on pgc.oid = a.attrelid
left join pg_description c on (pgc.oid = c.objoid and a.attnum = c.objsubid)
where a.attnum > 0
and pgc.oid = a.attrelid
and pg_table_is_visible(pgc.oid)
and not a.attisdropped
and pgc.relname = 'leaves'  -- Your table name here
order by a.attnum;

-- index stats



-- index description
