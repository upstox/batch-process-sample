package com.sample.trade.dataservice;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.batch.process.common.RangeInfo;
import com.batch.process.dataservice.AbstractPartitionInfoWithRangeDataService;
import com.batch.process.dataservice.ContextMap;
import com.sample.common.Constant;
import com.sample.common.TradeConstant;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TradeDataPartitionInfoWithRangeDataService extends AbstractPartitionInfoWithRangeDataService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static String rangeQuery = "SELECT min(id) as min_id, max(id) as max_id FROM " //
			+ Constant.QUERY_TOKEN_TEMP_DATABASE + "." + Constant.QUERY_TOKEN_TEMP_TABLE;

	@Override
	protected RangeInfo getRangeInfo(String jobId, Map<String, Object> parameterMap, ContextMap contextMap) {
		String tempTableName = contextMap.get(TradeConstant.CONTEXT_KEY_UCC_TEMP_TABLE);
		String dbName = "medium_demo";

		String query = rangeQuery.replace(Constant.QUERY_TOKEN_TEMP_DATABASE, dbName)
				.replace(Constant.QUERY_TOKEN_TEMP_TABLE, tempTableName);
		log.info("Range query | query:[{}]", query);

		List<RangeInfo> rangeInfoList = jdbcTemplate.query(query,
				(rs, rownum) -> RangeInfo.builder().maxId(rs.getLong("max_id")).minId(rs.getLong("min_id")).build());

		RangeInfo rangeInfo = rangeInfoList.get(0);

		if (rangeInfo.getMaxId() == 0) {
			String message = "No records found";
			log.warn("{} | min_id:[{}] | max_id: [{}]", message, rangeInfo.getMinId(), rangeInfo.getMaxId());
		}
		return rangeInfo;
	}

	@Override
	protected int getBatchCount(String jobId, Map<String, Object> parameterMap, ContextMap contextMap) {
		return 10;// Batch Constant for the type of job
	}

	@Override
	public String getDataServiceName() {
		return TradeDataPartitionInfoWithRangeDataService.class.getSimpleName();
	}
}
