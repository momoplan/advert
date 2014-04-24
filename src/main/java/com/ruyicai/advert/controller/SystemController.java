package com.ruyicai.advert.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.advert.service.CommonService;

@RequestMapping("/system")
@Controller
public class SystemController {

	@Autowired
	private CommonService commonService;
	
	@RequestMapping(value = "/parseMac", method = RequestMethod.GET)
	public @ResponseBody 
		String parseMac(@RequestParam("type") String type) {
		try {
			commonService.parseMac(type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}
	
}
