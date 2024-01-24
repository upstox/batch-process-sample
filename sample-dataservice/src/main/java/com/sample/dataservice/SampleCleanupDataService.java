package com.sample.dataservice;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.batch.process.dataservice.CleanUpDataService;
import com.batch.process.dataservice.ContextMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SampleCleanupDataService implements CleanUpDataService<String> {

    @Override
    public String getDataServiceName() {

        return SampleCleanupDataService.class.getSimpleName();
    }

    @Override
    public void cleanUp(String stepIdentifier, Map<String, Object> parameterMap, ContextMap contextMap) {
        log.info("SampleCleanupDataService cleanup");
    }

}
