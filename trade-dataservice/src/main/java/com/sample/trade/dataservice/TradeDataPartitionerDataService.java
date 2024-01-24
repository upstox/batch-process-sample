package com.sample.trade.dataservice;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.batch.process.common.PostPartitionData;
import com.batch.process.common.utils.storage.client.JsonFileClient;
import com.batch.process.dataservice.AbstractPrimarKeyRangeCountBasedPartitionListDataService;
import com.batch.process.dataservice.ContextMap;
import com.sample.common.Constant;
import com.sample.common.TradeConstant;
import com.sample.trade.model.TradeData;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TradeDataPartitionerDataService
		extends AbstractPrimarKeyRangeCountBasedPartitionListDataService<TradeData, PostPartitionData> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("fileClient")
	private JsonFileClient jsonFileClient;

	private static final String QUERY = ""//
			+ "select a.id, a.code, a.scrip_name, a.bs, a.qty from medium_demo.trade a inner join @temp_database@.@temp_table_name@ b on a.code=b.code "//
			+ "where b.id > @start_offset@ AND b.id <= @end_offset@";

	@Override
	protected String getDataFolderName() {
		return TradeConstant.CONTEXT_KEY_FOLDER_TRADE_DATA;
	}

	@Override
	protected void storePartitionedData(String jobId, String folderLocation, String partitionName,
			List<TradeData> dataModelList) {

		if (CollectionUtils.isEmpty(dataModelList)) {
			String message = "No records found";
			log.info("{} | code size :[{}]", message, dataModelList.size());
		}
		jsonFileClient.put(Constant.STORAGE_0, jobId + "/" + getDataFolderName(), partitionName, dataModelList);
	}

	@Override
	protected void savePostPartitionProcessData(String jobId, Map<String, Object> parametersMap, ContextMap contextMap,
			List<PostPartitionData> postPartitionProcessData) {
		// Not needed
	}

	@Override
	protected PostPartitionData processPartitionData(String jobId, Map<String, Object> parametersMap,
			ContextMap contextMap, String partitionName, List<TradeData> dataModelList) {
		return null;
	}

	@Override
	protected List<TradeData> getDataModelList(String jobId, String query, Map<String, Object> parametersMap,
			ContextMap contextMap) {
		return jdbcTemplate.query(query, (rs, rownum) -> TradeData.builder()//
				.id(rs.getInt("id"))
				.code(rs.getString("code"))//
				.scripName(rs.getString("scrip_name"))//
				.bs(rs.getString("bs"))//
				.qty(rs.getInt("qty"))//
				.build());
	}

	@Override
	protected String getDataQuery(String jobId, Map<String, Object> parametersMap, ContextMap contextMap) {
		String tempTableName = contextMap.get(TradeConstant.CONTEXT_KEY_UCC_TEMP_TABLE);
		String dbName = "medium_demo";
		return QUERY.replace(Constant.QUERY_TOKEN_TEMP_DATABASE, dbName).replace(Constant.QUERY_TOKEN_TEMP_TABLE,
				tempTableName);
	}

	@Override
	public String getDataServiceName() {
		return TradeDataPartitionerDataService.class.getSimpleName();
	}
}