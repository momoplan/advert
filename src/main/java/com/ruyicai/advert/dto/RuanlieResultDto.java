package com.ruyicai.advert.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import com.ruyicai.advert.consts.RuanlieErrorCode;

@RooJson
@RooJavaBean
public class RuanlieResultDto {

	private String code;
	
	private String desc;
	
	public RuanlieResultDto(RuanlieErrorCode errorCode) {
		this.code = errorCode.value;
		this.desc = errorCode.memo;
	}
	
}
