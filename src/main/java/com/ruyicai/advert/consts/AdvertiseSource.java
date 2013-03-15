package com.ruyicai.advert.consts;

public enum AdvertiseSource {

	limei("limei", "力美"),
	dianRu("diarnu", "点入"),
	duoMeng("domob", "多盟");
	
	private String value;
	
	private String memo;
	
	public String value() {
		return value;
	}
	
	public String memo() {
		return memo;
	}
	
	private AdvertiseSource(String value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
}
