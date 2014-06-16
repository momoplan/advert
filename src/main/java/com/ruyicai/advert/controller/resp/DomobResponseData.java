package com.ruyicai.advert.controller.resp;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.advert.consts.errorcode.DomobErrorCode;

@RooJson
@RooJavaBean
public class DomobResponseData {

	private boolean success;
	private String message;
	
	public DomobResponseData() {
	}
	
	public DomobResponseData(DomobErrorCode errorCode) {
		this.success = errorCode.value;
		this.message = errorCode.memo;
	}
	
}
