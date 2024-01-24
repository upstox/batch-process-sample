package com.sample.config;

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
import com.sample.common.SampleConstants;
import com.sample.dataservice.SampleCleanupDataService;
import com.sample.dataservice.SamplePartitionDataService;
import com.sample.dataservice.SamplePartitionInfoProvider;
import com.sample.dataservice.SamplePostWorkerDataService;
import com.sample.dataservice.SampleSingleDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleConfiguration extends AbstractBatchJobConfiguration {

	private final SampleSingleDataService sampleSingleDataService;
	private final SamplePartitionInfoProvider samplePartitionInfoProvider;
	private final SamplePartitionDataService samplePartitionDataService;
	private final SamplePostWorkerDataService samplePostWorkerDataService;
	private final SampleCleanupDataService sampleCleanupDataService;

	private final List<DataService<?>> dataServices = new ArrayList<>();

	/**
	 * Order the execution order
	 */
	@PostConstruct
	public void init() {
		dataServices.add(sampleSingleDataService);
		dataServices.add(samplePartitionInfoProvider);
		dataServices.add(samplePartitionDataService);
		dataServices.add(samplePostWorkerDataService);
		dataServices.add(sampleCleanupDataService);

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
		return SampleConstants.JOB_TYPE;
	}

	@Override
	public Map<String, DataService<?>> getDataServices() {
		Map<String, DataService<?>> dataServiceMap = new LinkedHashMap<>();
		dataServices.forEach(ds -> dataServiceMap.put(ds.getDataServiceName(), ds));
		return Collections.unmodifiableMap(dataServiceMap);
	}

	@Override
	public DataService<?> getCleanUpService() {
		return sampleCleanupDataService;
	}

	@Override
	public List<Problem> validate(Properties properties) {
		return Collections.emptyList();
	}

}
