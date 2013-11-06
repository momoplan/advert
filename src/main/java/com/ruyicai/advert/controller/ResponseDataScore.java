package com.ruyicai.advert.controller;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJson
@RooJavaBean
public class ResponseDataScore {

	private String code;
	private String message;
	
	public ResponseDataScore() {
	}
	
	public ResponseDataScore(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
}
