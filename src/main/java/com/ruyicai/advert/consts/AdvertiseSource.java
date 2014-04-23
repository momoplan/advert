package com.ruyicai.advert.consts;

import org.apache.commons.lang.StringUtils;

public enum AdvertiseSource {

	limei("limei", "力美", "936"),
	dianRu("diarnu", "点入", "937"),
	duoMeng("domob", "多盟", "1071"),
	ruanlie("ruanlie", "软猎", "1068"),
	mopan("mopan", "磨盘", "1066"),
	wangyu("wangyu", "网域", "1069"),
	yijifen("yijifen", "易积分", "1067");
	
	private String value;
	
	private String memo;
	
	private String coopId;
	
	public String value() {
		return value;
	}
	
	public String memo() {
		return memo;
	}
	
	public String coopId() {
		return coopId;
	}
	
	private AdvertiseSource(String value, String memo, String coopId) {
		this.value = value;
		this.memo = memo;
		this.coopId = coopId;
	}
	
	public static String getSourceByCoopId(String coopId) {
		if (StringUtils.isBlank(coopId)) {
			return "";
		}
		AdvertiseSource[] values = AdvertiseSource.values();
		for (AdvertiseSource advertiseSource : values) {
			if (StringUtils.equals(advertiseSource.coopId(), coopId)) {
				return advertiseSource.value();
			}
		}
		return "";
	}
	
}
