package com.ruyicai.advert.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PropertiesUtil {

	@Value("${limei.notifyUrl}")
	private String limei_notifyUrl;
	public String getLimei_notifyUrl() {
		return limei_notifyUrl;
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
	
}
