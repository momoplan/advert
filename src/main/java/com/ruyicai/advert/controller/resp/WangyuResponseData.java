package com.ruyicai.advert.controller.resp;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.advert.consts.errorcode.WangyuErrorCode;

@RooJson
@RooJavaBean
public class WangyuResponseData {

	private boolean success;
	private String message;
	
	public WangyuResponseData() {
	}
	
	public WangyuResponseData(WangyuErrorCode errorCode) {
		this.success = errorCode.value;
		this.message = errorCode.memo;
	}
	
}
