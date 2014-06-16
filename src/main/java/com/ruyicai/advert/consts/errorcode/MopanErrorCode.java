package com.ruyicai.advert.consts.errorcode;

public enum MopanErrorCode {

	success(true, "ok"), //成功
	paramException(false, "paramException"), //参数异常
	repeatRecord(false, "repeatRecord"), //重复记录
	hasActive(false, "hasActive"), //已激活
	exception(false, "Exception"); //未知错误
	
	public boolean value;
	
	public String memo;
	
	MopanErrorCode(boolean value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
}
