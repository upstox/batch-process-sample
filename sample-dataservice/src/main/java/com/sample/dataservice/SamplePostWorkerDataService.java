package com.sample.dataservice;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.batch.process.dataservice.ContextMap;
import com.batch.process.dataservice.SingleDataService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SamplePostWorkerDataService implements SingleDataService<String> {

    @Override
    public String getDataServiceName() {
        return SamplePostWorkerDataService.class.getSimpleName();
    }

    @Override
    public String getData(String jobId, Map<String, Object> parameterMap, ContextMap contextMap) {
        return "Post worker";
    }

    @Override
    public void storeData(String string, String data, Map<String, Object> parameterMap, ContextMap contextMap) {
        log.info("SamplePostWorkerDataService store");
    }

}
