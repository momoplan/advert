package com.ruyicai.advert.controller.resp;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.advert.consts.errorcode.YijifenErrorCode;

@RooJson
@RooJavaBean
public class YijifenResponseData {

	private boolean success;
	private String message;
	
	public YijifenResponseData() {
	}
	
	public YijifenResponseData(YijifenErrorCode errorCode) {
		this.success = errorCode.value;
		this.message = errorCode.memo;
	}
	
}
