package com.ruyicai.advert.exception;

import com.ruyicai.advert.consts.MopanErrorCode;

public class MopanException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private MopanErrorCode errorCode;
	
	public MopanException(String msg) {
		super(msg);
	}
	
	public MopanException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public MopanException(MopanErrorCode errorCode) {
		super(errorCode.memo);
		this.errorCode = errorCode;
	}
	
	public MopanErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(MopanErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
}
