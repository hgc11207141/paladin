package com.paladin.framework.exception;

public class RollbackException extends RuntimeException {

	private static final long serialVersionUID = -4876854211625812063L;

	public RollbackException() {
		super();
	}

	public RollbackException(String message) {
		super(message);
	}

	public RollbackException(String message, Throwable cause) {
		super(message, cause);
	}

	public RollbackException(Throwable cause) {
		super(cause);
	}

	protected RollbackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
