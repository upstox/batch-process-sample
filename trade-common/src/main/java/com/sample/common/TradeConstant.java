package com.sample.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TradeConstant {

	/*
	 * ContextMap keys
	 */
	public static final String CONTEXT_KEY_UCC_TEMP_TABLE = "UCC_TEMP_TABLE";
	public static final String CONTEXT_KEY_TEMP_DATABASE = "TEMP_DATABASE";

	public static final String TEMP_TABLE_NAME_PREFIX = "temp_trade_data";

	public static final String CONTEXT_KEY_FOLDER_TRADE_CODE_DATA = "code_data";
	public static final String CONTEXT_KEY_FOLDER_TRADE_DATA = "trade_data";

	// spring integration configurations
	public static final String MANAGER_OUT_CHANNEL = "tradeDataRequest";
	public static final String MANAGER_IN_CHANNEL = "tradeDataReplies";
	public static final String MANAGER_OUT_FLOW = "tradeDataRequestOutboundFlow";
	public static final String MANAGER_IN_FLOW = "tradeDataRequestInBoundFlow";
	public static final String WORKER_OUT_CHANNEL = "tradeDataReplies";
	public static final String WORKER_IN_CHANNEL = "tradeDataRequest";
	public static final String WORKER_OUT_FLOW = "tradeDataRequestOutboundFlow";
	public static final String WORKER_IN_FLOW = "tradeDataRequestInBoundFlow";
	public static final String WORKER_STEP_NAME = "tradeDataWorkerStep";

	public static final String FILE_TYPE = "tradeDataFileType";

	public static final String JOB_TYPE = "process-trade";

	public static final String CONTEXT_KEY_FOLDER_TRADE_DATA_PROCESSED = "processed_trades";

	public static final String DATA_FOLDER_PROCESSED_TRADES = "processed_trades";
	public static final String DATA_FILE_PROCESSED_TRADES_OUTPUT = "output.csv";
}