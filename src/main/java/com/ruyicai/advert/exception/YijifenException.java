package com.ruyicai.advert.exception;

import com.ruyicai.advert.consts.errorcode.YijifenErrorCode;

public class YijifenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private YijifenErrorCode errorCode;
	
	public YijifenException(String msg) {
		super(msg);
	}
	
	public YijifenException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public YijifenException(YijifenErrorCode errorCode) {
		super(errorCode.memo);
		this.errorCode = errorCode;
	}
	
	public YijifenErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(YijifenErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
}
