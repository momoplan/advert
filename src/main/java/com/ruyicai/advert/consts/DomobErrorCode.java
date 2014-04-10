package com.ruyicai.advert.consts;

public enum DomobErrorCode {

	success(true, "success"), //成功
	paramException(false, "paramException"), //参数异常
	exception(false, "Exception"); //未知错误
	
	public boolean value;
	
	public String memo;
	
	DomobErrorCode(boolean value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
}
