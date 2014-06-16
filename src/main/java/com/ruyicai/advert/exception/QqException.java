package com.ruyicai.advert.exception;

import com.ruyicai.advert.consts.QqErrorCode;

public class QqException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private QqErrorCode errorCode;
	
	public QqException(String msg) {
		super(msg);
	}
	
	public QqException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public QqException(QqErrorCode errorCode) {
		super(errorCode.memo);
		this.errorCode = errorCode;
	}
	
	public QqErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(QqErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
}
