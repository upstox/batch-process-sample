package com.sample.trade.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.batch.process.common.validation.model.Problem;
import com.batch.process.dataservice.DataService;
import com.batch.process.dataservice.configuration.AbstractBatchJobConfiguration;
import com.batch.process.listener.BatchJobListener;
import com.batch.process.listener.JobEvent;
import com.sample.common.TradeConstant;
import com.sample.trade.dataservice.DumpUniqueClientsInTempTableDataService;
import com.sample.trade.dataservice.TradeDataCleanupDataService;
import com.sample.trade.dataservice.TradeDataPartitionInfoWithRangeDataService;
import com.sample.trade.dataservice.TradeDataPartitionerDataService;
import com.sample.trade.dataservice.TradeDataCodePartitionerDataService;
import com.sample.trade.dataservice.TradeDataOutputDataService;
import com.sample.trade.dataservice.TradePartitionDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeProcessorConfiguration extends AbstractBatchJobConfiguration {

	private final DumpUniqueClientsInTempTableDataService dumpUniqueClientsInTempTableDataService;
	private final TradeDataPartitionInfoWithRangeDataService tradeDataPartitionInfoWithRangeDataService;
	private final TradeDataCodePartitionerDataService tradeDataCodePartitionerDataService;
	private final TradePartitionDataService tradePartitionDataService;
	private final TradeDataPartitionerDataService tradeDataPartitionerDataService;
	private final TradeDataOutputDataService tradeDataOutputDataService;

	private final TradeDataCleanupDataService tradeDataCleanupDataService;

	private final List<DataService<?>> dataServices = new ArrayList<>();

	/**
	 * Order the execution order
	 */
	@PostConstruct
	public void init() {
		dataServices.add(dumpUniqueClientsInTempTableDataService);
		dataServices.add(tradeDataPartitionInfoWithRangeDataService);
		dataServices.add(tradeDataCodePartitionerDataService);
		dataServices.add(tradeDataPartitionerDataService);
		dataServices.add(tradePartitionDataService);
		dataServices.add(tradeDataOutputDataService);

		addBatchJobListener(new BatchJobListener() {

			@Override
			public void beforeJob(JobEvent jobEvent) {
				log.info("Calling before job");
			}

			@Override
			public void afterJob(JobEvent jobEvent) {
				log.info("Calling after job");
			}
		});
	}

	@Override
	public String getJobType() {
		return TradeConstant.JOB_TYPE;
	}

	@Override
	public Map<String, DataService<?>> getDataServices() {
		Map<String, DataService<?>> dataServiceMap = new LinkedHashMap<>();
		dataServices.forEach(ds -> dataServiceMap.put(ds.getDataServiceName(), ds));
		return Collections.unmodifiableMap(dataServiceMap);
	}

	@Override
	public DataService<?> getCleanUpService() {
		return tradeDataCleanupDataService;
	}

	@Override
	public List<Problem> validate(Properties properties) {
		return Collections.emptyList();
	}

}
