package com.sample.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SampleConstants {
    // spring integration configurations
    public static final String MANAGER_OUT_CHANNEL = "sampleRequest";
    public static final String MANAGER_IN_CHANNEL = "sampleReplies";
    public static final String MANAGER_OUT_FLOW = "sampleRequestOutboundFlow";
    public static final String MANAGER_IN_FLOW = "sampleRequestInBoundFlow";
    public static final String WORKER_OUT_CHANNEL = "sampleReplies";
    public static final String WORKER_IN_CHANNEL = "sampleRequest";
    public static final String WORKER_OUT_FLOW = "sampleRequestOutboundFlow";
    public static final String WORKER_IN_FLOW = "sampleRequestInBoundFlow";
    public static final String WORKER_STEP_NAME = "sampleWorkerStep";
    public static final String FILE_TYPE = "sampleFileType";
    public static final String JOB_TYPE = "sampleJobType";

}
