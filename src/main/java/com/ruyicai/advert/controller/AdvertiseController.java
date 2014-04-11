package com.ruyicai.advert.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.advert.consts.DomobErrorCode;
import com.ruyicai.advert.consts.MopanErrorCode;
import com.ruyicai.advert.consts.RuanlieErrorCode;
import com.ruyicai.advert.consts.WangyuErrorCode;
import com.ruyicai.advert.consts.YijifenErrorCode;
import com.ruyicai.advert.controller.resp.DomobResponseData;
import com.ruyicai.advert.controller.resp.MopanResponseData;
import com.ruyicai.advert.controller.resp.RuanlieResponseData;
import com.ruyicai.advert.controller.resp.WangyuResponseData;
import com.ruyicai.advert.controller.resp.YijifenResponseData;
import com.ruyicai.advert.dto.RuanlieResultDto;
import com.ruyicai.advert.exception.DomobException;
import com.ruyicai.advert.exception.MopanException;
import com.ruyicai.advert.exception.RuanlieException;
import com.ruyicai.advert.exception.WangyuException;
import com.ruyicai.advert.exception.YijifenException;
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
		DomobResponseData domobNotify(HttpServletRequest request, @RequestParam("appId") String appId, 
				@RequestParam("mac") String mac,  @RequestParam("ifa") String idfa, 
				@RequestParam("source") String source) {
		DomobErrorCode errorCode = DomobErrorCode.success;
		try {
			long startTimeMillis = System.currentTimeMillis();
			String ip = request.getHeader("X-Forwarded-For");
			advertiseService.domobReceive(ip, appId, mac, idfa, source);
			long endTimeMillis = System.currentTimeMillis();
			logger.info("多盟广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",mac="+mac+",idfa="+idfa);
		} catch (DomobException e) {
			errorCode = e.getErrorCode();
			logger.error("多盟广告点击记录内部异常,message="+errorCode.memo+",mac="+mac+",idfa="+idfa);
		} catch (Exception e) {
			errorCode = DomobErrorCode.exception;
			logger.error("多盟广告点击记录发生异常,mac="+mac+",idfa="+idfa, e);
		}
		DomobResponseData rd = new DomobResponseData(errorCode);
		return rd;
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
	
	/**
	 * 磨盘广告
	 * @param mac
	 * @param appId
	 * @param source
	 * @return
	 */
	@RequestMapping(value = "/mopanNotify", method = RequestMethod.GET)
	public @ResponseBody 
		MopanResponseData mopanNotify(HttpServletRequest request, @RequestParam("appid") String appId, 
				@RequestParam("mac") String mac) {
		MopanErrorCode errorCode = MopanErrorCode.success;
		try {
			long startTimeMillis = System.currentTimeMillis();
			String ip = request.getHeader("X-Forwarded-For");
			advertiseService.mopanReceive(ip, appId, mac);
			long endTimeMillis = System.currentTimeMillis();
			logger.info("磨盘广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",mac="+mac);
		} catch (MopanException e) {
			errorCode = e.getErrorCode();
			logger.error("磨盘广告点击记录内部异常,message="+errorCode.memo+",mac="+mac);
		} catch (Exception e) {
			errorCode = MopanErrorCode.exception;
			logger.error("磨盘广告点击记录发生异常,mac="+mac, e);
		}
		MopanResponseData rd = new MopanResponseData(errorCode);
		return rd;
	}
	
	/**
	 * 网域广告
	 * @param mac
	 * @param appId
	 * @param source
	 * @return
	 */
	@RequestMapping(value = "/wangyuNotify", method = RequestMethod.GET)
	public @ResponseBody 
		WangyuResponseData wangyuNotify(HttpServletRequest request, @RequestParam("cid") String cid, 
				@RequestParam("deviceid") String deviceid) {
		WangyuErrorCode errorCode = WangyuErrorCode.success;
		try {
			long startTimeMillis = System.currentTimeMillis();
			String ip = request.getHeader("X-Forwarded-For");
			advertiseService.wangyuReceive(ip, cid, deviceid);
			long endTimeMillis = System.currentTimeMillis();
			logger.info("网域广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",mac="+deviceid);
		} catch (WangyuException e) {
			errorCode = e.getErrorCode();
			logger.error("网域广告点击记录内部异常,message="+errorCode.memo+",mac="+deviceid);
		} catch (Exception e) {
			errorCode = WangyuErrorCode.exception;
			logger.error("网域广告点击记录发生异常,mac="+deviceid, e);
		}
		WangyuResponseData rd = new WangyuResponseData(errorCode);
		return rd;
	}
	
	/**
	 * 易积分广告
	 * @param mac
	 * @param appId
	 * @param source
	 * @return
	 */
	@RequestMapping(value = "/yijifenNotify", method = RequestMethod.GET)
	public @ResponseBody 
		YijifenResponseData yijifenNotify(HttpServletRequest request, @RequestParam("appid") String appId, 
				@RequestParam("deviceid") String deviceid, @RequestParam("source") String source,
				@RequestParam("IDFV") String idfa) {
		YijifenErrorCode errorCode = YijifenErrorCode.success;
		try {
			long startTimeMillis = System.currentTimeMillis();
			String ip = request.getHeader("X-Forwarded-For");
			advertiseService.yijifenReceive(ip, appId, deviceid, source, idfa);
			long endTimeMillis = System.currentTimeMillis();
			logger.info("易积分广告点击记录用时:"+(endTimeMillis-startTimeMillis)+",mac="+deviceid);
		} catch (YijifenException e) {
			errorCode = e.getErrorCode();
			logger.error("易积分广告点击记录内部异常,message="+errorCode.memo+",mac="+deviceid);
		} catch (Exception e) {
			errorCode = YijifenErrorCode.exception;
			logger.error("易积分广告点击记录发生异常,mac="+deviceid, e);
		}
		YijifenResponseData rd = new YijifenResponseData(errorCode);
		return rd;
	}
	
}
