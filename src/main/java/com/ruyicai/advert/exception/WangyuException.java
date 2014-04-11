package com.ruyicai.advert.exception;

import com.ruyicai.advert.consts.WangyuErrorCode;

public class WangyuException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private WangyuErrorCode errorCode;
	
	public WangyuException(String msg) {
		super(msg);
	}
	
	public WangyuException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public WangyuException(WangyuErrorCode errorCode) {
		super(errorCode.memo);
		this.errorCode = errorCode;
	}
	
	public WangyuErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(WangyuErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
}
