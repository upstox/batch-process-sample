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
import com.sample.trade.model.CodeData;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TradeDataCodePartitionerDataService
		extends AbstractPrimarKeyRangeCountBasedPartitionListDataService<CodeData, PostPartitionData> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("fileClient")
	private JsonFileClient jsonFileClient;

	private static final String QUERY = "select id, code from @temp_database@.@temp_table_name@ where id > @start_offset@ AND id <= @end_offset@";

	@Override
	protected String getDataFolderName() {
		return TradeConstant.CONTEXT_KEY_FOLDER_TRADE_CODE_DATA;
	}

	@Override
	protected void storePartitionedData(String jobId, String folderLocation, String partitionName,
			List<CodeData> dataModelList) {

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
			ContextMap contextMap, String partitionName, List<CodeData> dataModelList) {
		return null;
	}

	@Override
	protected List<CodeData> getDataModelList(String jobId, String query, Map<String, Object> parametersMap,
			ContextMap contextMap) {

		return jdbcTemplate.query(query, (rs, rownum) -> CodeData.builder()//
				.id(rs.getInt("id"))//
				.code(rs.getString("code"))//
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
		return TradeDataCodePartitionerDataService.class.getSimpleName();
	}
}