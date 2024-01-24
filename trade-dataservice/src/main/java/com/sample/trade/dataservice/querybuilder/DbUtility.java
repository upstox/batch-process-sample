package com.sample.trade.dataservice.querybuilder;

import com.sample.common.Constant;

public class DbUtility {
	
	private DbUtility(){
		
	}

	public static String getTempTableName(String tableNamePrefix, String jobId) {
		return String.format("%s_%s", tableNamePrefix, jobId);
	}
	
	public static String getTableTokenReplacedQuery(String table, String query, String database) {
        return query.replace(Constant.QUERY_TOKEN_TEMP_TABLE, table)
                .replace(Constant.QUERY_TOKEN_TEMP_DATABASE, database);
    }

}
