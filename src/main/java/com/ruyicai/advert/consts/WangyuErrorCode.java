package com.ruyicai.advert.consts;

public enum WangyuErrorCode {

	success(true, "成功"), //成功
	paramException(false, "参数有误"), //参数异常
	repeatRecord(false, "重复提交"), //重复记录
	hasActive(false, "已激活"), //已激活
	exception(false, "保存失败"); //未知错误
	
	public boolean value;
	
	public String memo;
	
	WangyuErrorCode(boolean value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
}
