package com.ruyicai.advert.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.advert.consts.RuanlieErrorCode;
import com.ruyicai.advert.controller.resp.RuanlieResponseData;
import com.ruyicai.advert.dto.RuanlieResultDto;
import com.ruyicai.advert.exception.RuanlieException;
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
			long startTimeMillis = System.currentTimeMillis();
			String ip = request.getHeader("X-Forwarded-For");
			ResponseData response = advertiseService.limeiReceive(ip, mac, appId, source);
			long endTimeMillis = System.currentTimeMillis();
			logger.info("力美广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",mac="+mac);
			return response;
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
			long startTimeMillis = System.currentTimeMillis();
			String ip = request.getHeader("X-Forwarded-For");
			ResponseData response = advertiseService.dianruReceive(ip, drkey, source);
			long endTimeMillis = System.currentTimeMillis();
			logger.info("点入广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",drkey="+drkey);
			return response;
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
	@RequestMapping(value = "/domobNotify", method = RequestMethod.GET)
	public @ResponseBody 
		ResponseData domobNotify(HttpServletRequest request, @RequestParam("udid") String mac, 
				@RequestParam("app") String appId,  @RequestParam("source") String source, 
				@RequestParam("returnFormat") String returnFormat) {
		try {
			long startTimeMillis = System.currentTimeMillis();
			String ip = request.getHeader("X-Forwarded-For");
			ResponseData response = advertiseService.domobReceive(ip, mac, appId, source, returnFormat);
			long endTimeMillis = System.currentTimeMillis();
			logger.info("多盟广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",mac="+mac);
			return response;
		} catch (Exception e) {
			logger.error("多盟广告点击记录发生异常,mac="+mac+",appId="+appId, e);
			return new ResponseData(false, "通知失败");
		}
	}
	
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
			@RequestParam(value = "appid") String appId, @RequestParam(value = "source") String source) {
		try {
			long startTimeMillis = System.currentTimeMillis();
			String ip = request.getHeader("X-Forwarded-For");
			ResponseData response = advertiseService.miidiReceive(ip, mac, appId, source);
			long endTimeMillis = System.currentTimeMillis();
			logger.info("米迪广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",mac="+mac);
			return response;
		} catch (Exception e) {
			logger.error("米迪广告点击记录发生异常,mac="+mac+",appId="+appId, e);
			return new ResponseData(false, "通知失败");
		}
	}
	
	/**
	 * 软猎广告
	 * @param request
	 * @param mac
	 * @param appid
	 * @param source
	 * @return
	 */
	@RequestMapping(value = "/ruanlieNotify", method = RequestMethod.GET)
	public @ResponseBody
		RuanlieResponseData ruanlieNotify(HttpServletRequest request, @RequestParam(value = "mac") String mac, 
				@RequestParam(value = "idfa") String idfa, @RequestParam(value = "appId") String appId) {
		RuanlieResponseData rd = new RuanlieResponseData();
		RuanlieErrorCode errorCode = RuanlieErrorCode.success;
		try {
			long startTimeMillis = System.currentTimeMillis();
			String ip = request.getHeader("X-Forwarded-For");
			advertiseService.ruanlieReceive(ip, mac, idfa, appId);
			long endTimeMillis = System.currentTimeMillis();
			logger.info("软猎广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",mac="+mac+",idfa="+idfa);
		} catch (RuanlieException e) {
			errorCode = e.getErrorCode();
			logger.error("软猎广告点击记录内部异常,code="+errorCode.value+",desc="+errorCode.memo+",mac="+mac+",idfa="+idfa);
		} catch (Exception e) {
			errorCode = RuanlieErrorCode.exception;
			logger.error("软猎广告点击记录发生异常,mac="+mac+",idfa="+idfa, e);
		}
		RuanlieResultDto dto = new RuanlieResultDto(errorCode);
		rd.setResult(dto);
		return rd;
	}
	
}
