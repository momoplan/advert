package com.ruyicai.advert.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PropertiesUtil {

	@Value("${limei.ip}")
	private String limei_ip;
	public String getLimei_ip() {
		return limei_ip;
	}
	
	@Value("${limei.notifyUrl}")
	private String limei_notifyUrl;
	public String getLimei_notifyUrl() {
		return limei_notifyUrl;
	}
	
	@Value("${limei.android.aduid}")
	private String limeiAndroidAduid;
	public String getLimeiAndroidAduid() {
		return limeiAndroidAduid;
	}

	@Value("${dianjoy.salt}")
	private String dianjoy_salt;
	public String getDianjoy_salt() {
		return dianjoy_salt;
	}

	@Value("${dianjoy.notifyUrl}")
	private String dianjoy_notifyUrl;
	public String getDianjoy_notifyUrl() {
		return dianjoy_notifyUrl;
	}
	
	@Value("${dianru.ip}")
	private String dianru_ip;
	public String getDianru_ip() {
		return dianru_ip;
	}
	
	@Value("${dianru.notifyUrl}")
	private String dianru_notifyUrl;
	public String getDianru_notifyUrl() {
		return dianru_notifyUrl;
	}
	
	@Value("${domob.notifyUrl}")
	private String domob_notifyUrl;
	public String getDomob_notifyUrl() {
		return domob_notifyUrl;
	}
	
	@Value("${miidi.notifyUrl}")
	private String miidi_notifyUrl;
	public String getMiidi_notifyUrl() {
		return miidi_notifyUrl;
	}
	
	@Value("${lotteryUrl}")
	private String lotteryUrl;
	public String getLotteryUrl() {
		return lotteryUrl;
	}
	
}
