package com.sample.dataservice;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.batch.process.dataservice.AbstractPartitionInfoDataService;
import com.batch.process.dataservice.ContextMap;

@Component
public class SamplePartitionInfoProvider extends AbstractPartitionInfoDataService {

    @Override
    public String getDataServiceName() {
        return SamplePartitionInfoProvider.class.getSimpleName();
    }

    @Override
    protected long getTotalCount(String jobId, Map<String, Object> parameterMap, ContextMap contextMap) {
        return 100;
    }

    @Override
    protected int getBatchCount(String jobId, Map<String, Object> parameterMap, ContextMap contextMap) {
        return 10;
    }
}
