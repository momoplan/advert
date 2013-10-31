package com.ruyicai.advert.controller;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJson
@RooJavaBean
public class ResponseData {

	private boolean success;
	private String message;
	
	public ResponseData() {
	}
	
	public ResponseData(boolean success, String message) {
		this.success = success;
		this.message = message;
	}
	
}
