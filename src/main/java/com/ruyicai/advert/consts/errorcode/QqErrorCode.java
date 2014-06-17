package com.ruyicai.advert.consts.errorcode;

public enum QqErrorCode {

	success("0", "步骤已完成或奖励发放成功"),
	userNotEsist("1", "查找不到该用户"),
	notFinish("2", "用户尚未完成本步骤"),
	awardHasGive("3", "该步骤奖励已发放过"),
	awardGiveFail("102", "奖励发放失败"),
	paramError("103", "请求参数错误"),
	exception("500", "服务器异常");
	
	public String value;
	
	public String memo;
	
	QqErrorCode(String value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
}