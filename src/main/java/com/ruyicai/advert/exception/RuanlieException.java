package com.ruyicai.advert.exception;

import com.ruyicai.advert.consts.RuanlieErrorCode;

public class RuanlieException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private RuanlieErrorCode errorCode;
	
	public RuanlieException(String msg) {
		super(msg);
	}
	
	public RuanlieException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public RuanlieException(RuanlieErrorCode errorCode) {
		super(errorCode.memo);
		this.errorCode = errorCode;
	}
	
	public RuanlieErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(RuanlieErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
}
