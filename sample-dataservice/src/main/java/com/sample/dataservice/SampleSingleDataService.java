package com.sample.dataservice;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.batch.process.dataservice.ContextMap;
import com.batch.process.dataservice.SingleDataService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SampleSingleDataService implements SingleDataService<String> {

    @Override
    public String getDataServiceName() {
        return SampleSingleDataService.class.getSimpleName();
    }

    @Override
    public String getData(String jobId, Map<String, Object> parameterMap, ContextMap contextMap) {

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            log.error("Exception occurred", e);
        }
        return "Got Something";
    }

    @Override
    public void storeData(String string, String data, Map<String, Object> parameterMap, ContextMap contextMap) {
        log.info("Store Something");
    }

}
