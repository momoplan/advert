package com.ruyicai.advert.consts;

public enum RuanlieErrorCode {

	success("000000", "ok"), //成功
	exception("100000", "Exception"), //未知错误
	paramException("100001", "paramException"); //参数异常
	
	public String value;
	
	public String memo;
	
	RuanlieErrorCode(String value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
}
