package com.ruyicai.advert.consts;

public enum CoopId {

	appStore_guanFang("889", "appStore官方版");
	//limei_zhuCe("936", "力美注册"),
	//dianRu_zhuCe("937", "点入注册"),
	//duoMeng_zhuCe("938", "多盟注册");
	
	
	private String value;
	
	private String memo;
	
	public String value() {
		return value;
	}
	
	public String memo() {
		return memo;
	}
	
	private CoopId(String value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
}
