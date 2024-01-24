package com.sample.common;

public class JobProcessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JobProcessException() {
		super();

	}

	public JobProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JobProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	public JobProcessException(String message) {
		super(message);
	}

	public JobProcessException(Throwable cause) {
		super(cause);
	}

}
