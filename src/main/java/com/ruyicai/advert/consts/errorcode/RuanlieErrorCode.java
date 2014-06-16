package com.ruyicai.advert.consts.errorcode;

public enum RuanlieErrorCode {

	success("000000", "ok"), //成功
	paramException("100001", "paramException"), //参数异常
	repeatRecord("100000", "repeatRecord"), //重复记录
	hasActive("100000", "hasActive"), //已激活
	exception("100000", "Exception"); //未知错误
	
	public String value;
	
	public String memo;
	
	RuanlieErrorCode(String value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
}
