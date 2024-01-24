package com.sample.trade.dataservice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.batch.process.common.utils.file.FileUtils;
import com.batch.process.common.utils.storage.client.BinaryFileClient;
import com.batch.process.common.utils.storage.client.JsonFileClient;
import com.batch.process.dataservice.ContextMap;
import com.batch.process.dataservice.ListDataService;
import com.sample.common.Constant;
import com.sample.common.JobProcessException;
import com.sample.common.TradeConstant;
import com.sample.trade.model.TradeData;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TradeDataOutputDataService implements ListDataService<String> {
	private final JsonFileClient jsonFileClient;
	private final BinaryFileClient binaryFileClient;

	public TradeDataOutputDataService(@Qualifier("fileClient") JsonFileClient jsonFileClient,
			@Qualifier("binaryClient") BinaryFileClient binaryFileClient) {
		this.jsonFileClient = jsonFileClient;
		this.binaryFileClient = binaryFileClient;
	}

	@Override
	public String getDataServiceName() {
		return TradeDataOutputDataService.class.getSimpleName();
	}

	@Override
	public List<String> getData(String jobId, Map<String, Object> parameterMap, ContextMap contextMap) {
		return jsonFileClient.get(Constant.STORAGE_0,
				jobId + Constant.SLASH + TradeConstant.DATA_FOLDER_PROCESSED_TRADES);
	}

	@Override
	public void storeData(String jobId, List<String> outputLocations, Map<String, Object> parameterMap,
			ContextMap contextMap) {
		try {
			File processedTradesFolder = Files.createTempDirectory("processed_output").toFile();
			File processedTradesFile = File.createTempFile("output.csv", "", processedTradesFolder);
			String outputFilePath = processedTradesFile.getAbsolutePath();
			File dest = new File(outputFilePath.substring(0, outputFilePath.lastIndexOf("/")),
					TradeConstant.DATA_FILE_PROCESSED_TRADES_OUTPUT);
			processedTradesFile.renameTo(dest);
			try (FileWriter fw = new FileWriter(dest); //
					BufferedWriter bw = new BufferedWriter(fw);) {
				bw.write("id,code,scrip,bs,qty,delQty");
				bw.newLine();
				for (String outputLocation : outputLocations) {
					List<TradeData> trades = jsonFileClient.getModel(outputLocation, TradeData.class);
					for (TradeData tradeData : trades) {
						bw.write(tradeData.toCsv());
						bw.newLine();
					}
				}
			}
			binaryFileClient.put(Constant.STORAGE_0,
					jobId + Constant.SLASH + TradeConstant.DATA_FILE_PROCESSED_TRADES_OUTPUT, dest.getAbsolutePath());
			FileUtils.deleteTempFiles(List.of(processedTradesFile.getAbsolutePath()));
		} catch (IOException e) {
			String msg = "Error creating trade output folder structure in temp";
			log.error(msg, e);
			throw new JobProcessException(msg);
		}
	}

}
