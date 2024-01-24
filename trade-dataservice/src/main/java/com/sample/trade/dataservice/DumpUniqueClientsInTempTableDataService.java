package com.sample.trade.dataservice;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.batch.process.dataservice.ContextMap;
import com.batch.process.dataservice.SingleDataService;
import com.sample.common.TradeConstant;
import com.sample.trade.dataservice.querybuilder.ClientTempTableCreationQueryBuilder;
import com.sample.trade.dataservice.querybuilder.ClientTempTableInsertionQueryBuilder;
import com.sample.trade.dataservice.querybuilder.DbUtility;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DumpUniqueClientsInTempTableDataService implements SingleDataService<Object> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ClientTempTableCreationQueryBuilder codeTempTableCreationQueryBuilder;

	@Autowired
	private ClientTempTableInsertionQueryBuilder codeTempTableInsertionQueryBuilder;

	@Override
	public Object getData(String jobId, Map<String, Object> parameterMap, ContextMap contextMap) {
		String tempTableName = DbUtility.getTempTableName(TradeConstant.TEMP_TABLE_NAME_PREFIX, jobId);
		contextMap.put(TradeConstant.CONTEXT_KEY_UCC_TEMP_TABLE, tempTableName);

		String query = codeTempTableCreationQueryBuilder.buildTempTableCreationQuery(contextMap);
		log.info("Temp Table query | query:[{}]", query);
		jdbcTemplate.execute(query);

		return null;
	}

	@Override
	public void storeData(String string, Object data, Map<String, Object> parameterMap, ContextMap contextMap) {
		String query = codeTempTableInsertionQueryBuilder.buildQuery(contextMap);
		log.info("Unique code query | query:[{}]", query);

		int insertedRecords = jdbcTemplate.update(query);

		log.info("Unique code Inserted in Temp Table | tempTableUccCount:[{}]", insertedRecords);

	}

	@Override
	public String getDataServiceName() {
		return DumpUniqueClientsInTempTableDataService.class.getSimpleName();
	}

}