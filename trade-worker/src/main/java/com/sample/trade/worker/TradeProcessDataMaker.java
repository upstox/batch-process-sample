package com.sample.trade.worker;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.batch.process.common.utils.file.FileUtils;
import com.batch.process.common.utils.storage.client.JsonFileClient;
import com.sample.common.Constant;
import com.sample.common.TradeConstant;
import com.sample.trade.model.CodeData;
import com.sample.trade.model.TradeData;

@Component
public class TradeProcessDataMaker {

	private JsonFileClient jsonFileClient;

	public TradeProcessDataMaker(@Qualifier(Constant.FILE_CLIENT) JsonFileClient jsonFileClient) {
		this.jsonFileClient = jsonFileClient;
	}

	public List<CodeData> getCodeData(Map<String, Object> metaInfo) {
		List<String> paths = jsonFileClient.get(Constant.STORAGE_0,
				metaInfo.get(TradeConstant.CONTEXT_KEY_FOLDER_TRADE_CODE_DATA).toString());
		List<CodeData> codeData = jsonFileClient.getModel(paths.get(0), CodeData.class);
		FileUtils.deleteTempFilesAndEmptyParentFolders(paths);

		return codeData;
	}

	public List<TradeData> getTrades(Map<String, Object> metaInfo) {
		List<String> paths = jsonFileClient.get(Constant.STORAGE_0,
				metaInfo.get(TradeConstant.CONTEXT_KEY_FOLDER_TRADE_DATA).toString());
		List<TradeData> trades = jsonFileClient.getModel(paths.get(0), TradeData.class);
		FileUtils.deleteTempFilesAndEmptyParentFolders(paths);

		return trades;
	}

}