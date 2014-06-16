package com.ruyicai.advert.exception;

import com.ruyicai.advert.consts.errorcode.DomobErrorCode;

public class DomobException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private DomobErrorCode errorCode;
	
	public DomobException(String msg) {
		super(msg);
	}
	
	public DomobException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public DomobException(DomobErrorCode errorCode) {
		super(errorCode.memo);
		this.errorCode = errorCode;
	}
	
	public DomobErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(DomobErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
}
