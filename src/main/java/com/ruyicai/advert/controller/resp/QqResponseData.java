package com.ruyicai.advert.controller.resp;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJson
@RooJavaBean
public class QqResponseData {

	private String ret;
	private String msg;
	private String zoneid;
	
	public QqResponseData() {
	}
	
	public QqResponseData(String ret, String msg, String zoneid) {
		this.ret = ret;
		this.msg = msg;
		this.zoneid = zoneid;
	}
	
}
