package com.sample.trade.dataservice;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.batch.process.dataservice.CleanUpDataService;
import com.batch.process.dataservice.ContextMap;
import com.sample.common.TradeConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TradeDataCleanupDataService implements CleanUpDataService<String> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public String getDataServiceName() {

		return TradeDataCleanupDataService.class.getSimpleName();
	}

	@Override
	public void cleanUp(String stepIdentifier, Map<String, Object> parameterMap, ContextMap contextMap) {
		log.info("SampleCleanupDataService cleanup");
		String tempTable = contextMap.get(TradeConstant.CONTEXT_KEY_UCC_TEMP_TABLE);
		log.info("Drop temp table | table:[{}]", tempTable);
		jdbcTemplate.execute("drop table " + "medium_demo." + tempTable);
		log.info("Dropped temp table | table:[{}]", tempTable);
	}

}
