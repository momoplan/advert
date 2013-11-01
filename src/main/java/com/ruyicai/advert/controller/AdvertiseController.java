package com.ruyicai.advert.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.advert.service.AdvertiseService;

/**
 * 广告点击记录Controller
 * @author Administrator
 *
 */
@RequestMapping("/advertise")
@Controller
public class AdvertiseController {

	private Logger logger = Logger.getLogger(AdvertiseController.class);
	
	@Autowired
	private AdvertiseService advertiseService;
	
	/**
	 * 力美广告
	 * @param mac
	 * @param advertiseId
	 * @param source
	 * @return
	 */
	@RequestMapping(value = "/limeiNotify", method = RequestMethod.GET)
	public @ResponseBody 
		ResponseData limeiNotify(HttpServletRequest request, @RequestParam("mac") String mac, 
			@RequestParam("appId") String appId, @RequestParam("source") String source) {
		try {
			return advertiseService.limeiNotify(request, mac, appId, source);
		} catch (Exception e) {
			logger.error("力美广告点击记录发生异常,mac="+mac+",appId="+appId, e);
			return new ResponseData(false, "通知失败");
		}
	}
	
	/**
	 * 点入广告
	 * @param drkey
	 * @param source
	 * @return
	 */
	@RequestMapping(value = "/dianruNotify", method = RequestMethod.GET)
	public @ResponseBody 
		ResponseData dianruNotify(HttpServletRequest request, @RequestParam("drkey") String drkey, 
			@RequestParam("source") String source) {
		try {
			return advertiseService.dianruNotify(request, drkey, source);
		} catch (Exception e) {
			logger.error("点入广告点击记录发生异常,drkey="+drkey, e);
			return new ResponseData(false, "通知失败");
		}
	}
	
	/**
	 * 多盟广告
	 * @param mac
	 * @param appId
	 * @param source
	 * @return
	 */
	/*@RequestMapping(value = "/domobNotify", method = RequestMethod.GET)
	public @ResponseBody 
		ResponseData domobNotify(@RequestParam("udid") String mac, @RequestParam("app") String appId,  
			@RequestParam("source") String source, @RequestParam("returnFormat") String returnFormat) {
		try {
			return advertiseService.domobNotify(mac, appId, source, returnFormat);
		} catch (Exception e) {
			logger.error("多盟广告点击记录发生异常,mac="+mac+",appId="+appId, e);
			return new ResponseData(false, "通知失败");
		}
	}*/
	
	/**
	 * 米迪广告
	 * @param request
	 * @param mac
	 * @param appid
	 * @param source
	 * @return
	 */
	@RequestMapping(value = "/miidiNotify", method = RequestMethod.GET)
	public @ResponseBody
		ResponseData miidiNotify(HttpServletRequest request, @RequestParam(value = "mac") String mac, 
			@RequestParam(value = "appid") String appid, @RequestParam(value = "source") String source) {
		try {
			return advertiseService.miidiNotify(request, mac, appid, source);
		} catch (Exception e) {
			logger.error("米迪广告点击记录发生异常,mac="+mac+",appid="+appid, e);
			return new ResponseData(false, "通知失败");
		}
	}
	
}
