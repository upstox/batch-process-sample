insert into @temp_database@.@temp_table_name@
select distinct medium_demo.trade.code a, NULL
from medium_demo.trade;