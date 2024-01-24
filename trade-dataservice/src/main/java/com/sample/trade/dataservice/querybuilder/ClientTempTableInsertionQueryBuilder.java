package com.sample.trade.dataservice.querybuilder;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.batch.process.dataservice.ContextMap;
import com.sample.common.Constant;
import com.sample.common.TradeConstant;

@Component
public class ClientTempTableInsertionQueryBuilder {

	@Value("classpath:insert_code_temp_table.sql")
	private Resource insertCodeResource;

	private String queryForInsert = null;

	@PostConstruct
	public void init() {
		queryForInsert = QueryReaderUtil.readQueryTemplate(insertCodeResource);
	}

	public String buildQuery(ContextMap contextMap) {
		String tempTableName = contextMap.get(TradeConstant.CONTEXT_KEY_UCC_TEMP_TABLE);
		String bouncedNotificationTempDb = "medium_demo";

		return queryForInsert.replace(Constant.QUERY_TOKEN_TEMP_DATABASE, bouncedNotificationTempDb)
				.replace(Constant.QUERY_TOKEN_TEMP_TABLE, tempTableName);
	}
}