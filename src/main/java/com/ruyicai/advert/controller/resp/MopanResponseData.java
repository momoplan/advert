package com.ruyicai.advert.controller.resp;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.advert.consts.errorcode.MopanErrorCode;

@RooJson
@RooJavaBean
public class MopanResponseData {

	private boolean success;
	private String message;
	
	public MopanResponseData() {
	}
	
	public MopanResponseData(MopanErrorCode errorCode) {
		this.success = errorCode.value;
		this.message = errorCode.memo;
	}
	
}
