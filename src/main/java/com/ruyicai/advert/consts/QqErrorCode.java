package com.ruyicai.advert.consts;

public enum QqErrorCode {

	success("0", "步骤已完成或奖励发放成功"),
	exception("500", "服务器异常");
	
	public String value;
	
	public String memo;
	
	QqErrorCode(String value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
}
