package com.sample.common.util;

import com.sample.common.Constant;

public class DataFileLocationUtil {
	private DataFileLocationUtil() {

	}

	public static String getDataFile(String partition, String jobId, String folderName) {
		return jobId + Constant.SLASH + folderName + Constant.SLASH + partition;
	}
}
