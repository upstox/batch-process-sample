package com.sample.manager;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SampleJobController {

    @Autowired
    @Qualifier(SampleBatchMQConfiguration.BEAN_MQ_JMS_TEMPLATE)
    private JmsTemplate jmsTemplate;

    @Value("${batch.framework.queue.unique.name}")
    private String batchQueueInputName;

    @PostMapping(value = "process", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> sendNotification(@RequestBody Map<String, Object> request) {
        log.info("Send notification request received: {}", request);
        jmsTemplate.convertAndSend(batchQueueInputName, request);
        log.info("Sent request to job queue successful");
        return ResponseEntity.ok()
                .body(true);
    }
}
