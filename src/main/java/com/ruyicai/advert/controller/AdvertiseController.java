package com.ruyicai.advert.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.util.PropertiesUtil;
import com.ruyicai.advert.util.StringUtil;
import com.ruyicai.advert.util.VerifyUtil;

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
	private PropertiesUtil propertiesUtil;
	
	/**
	 * 力美广告
	 * @param mac
	 * @param advertiseId
	 * @param source
	 * @return
	 */
	@RequestMapping(value = "/limeiNotify", method = RequestMethod.GET)
	public @ResponseBody String limeiNotify(HttpServletRequest request, @RequestParam("mac") String mac, 
			@RequestParam("appId") String appId, @RequestParam("source") String source) {
		JSONObject responseJson = new JSONObject();
		try {
			String ip = request.getHeader("X-Forwarded-For");
			logger.info("力美广告点击记录 start mac="+mac+";appId="+appId+";source="+source+";ip="+ip);
			//验证ip
			
			//将mac(28E02CE34713)地址加上":"(力美的mac格式:不加密,不带分隔符,大写)
			if (StringUtils.isNotBlank(mac)) {
				//mac = StringUtil.joinStringArrayWithCharacter(StringUtil.getStringArrayFromString(mac, 2), ":");
				mac = StringUtils.join(StringUtil.getStringArrayFromString(mac, 2), ":");
			}
			List<AdvertiseInfo> list = getAdvertiseInfoListByAppid(mac, appId, source);
			if (list==null||list.size()==0) {
				//保存记录
				saveAdvertiseInfoByAppid(mac, appId, source);
				return responseSuccess(responseJson, true, "通知成功");
			} else {
				return responseSuccess(responseJson, false, "重复记录");
			}
		} catch (Exception e) {
			logger.error("力美广告点击记录发生异常", e);
		}
		//logger.info("力美广告点击记录 end mac="+mac+";appId="+appId+";source="+source+",result="+responseJson.toString());
		return responseSuccess(responseJson, false, "通知失败");
	}
	
	/**
	 * 点入广告
	 * @param drkey
	 * @param source
	 * @return
	 */
	@RequestMapping(value = "/dianruNotify", method = RequestMethod.GET)
	public @ResponseBody String dianruNotify(HttpServletRequest request, @RequestParam("drkey") String drkey, 
			@RequestParam("source") String source) {
		JSONObject responseJson = new JSONObject();
		try {
			String ip = request.getHeader("X-Forwarded-For");
			logger.info("点入广告点击记录 start drkey="+drkey+";source="+source+";ip="+ip);
			//验证ip
			boolean verfyIp = VerifyUtil.verfyIp(ip, propertiesUtil.getDianru_ip());
			if (!verfyIp) {
				logger.error("点入广告点击记录,ip不合法 drkey="+drkey+";source="+source+";ip="+ip);
				return responseSuccess(responseJson, false, "ip不合法");
			}
			//验证参数
			if (StringUtils.isBlank(drkey) || drkey.length()<32) {
				return responseSuccess(responseJson, false, "参数错误");
			}
			String mac = StringUtils.substring(drkey, 32); //mac地址
			List<AdvertiseInfo> list = getAdvertiseInfoListByDrkey(mac, drkey, source);
			if (list==null||list.size()==0) {
				//保存记录
				saveAdvertiseInfoByDrkey(mac, drkey, source);
				return responseSuccess(responseJson, true, "通知成功");
			} else {
				return responseSuccess(responseJson, false, "重复记录");
			}
		} catch (Exception e) {
			logger.error("点入广告点击记录发生异常", e);
		}
		//logger.info("点入广告点击记录 end drkey="+drkey+";source="+source);
		return responseSuccess(responseJson, false, "通知失败");
	}
	
	/**
	 * 多盟广告
	 * @param mac
	 * @param appId
	 * @param source
	 * @return
	 */
	@RequestMapping(value = "/domobNotify", method = RequestMethod.GET)
	public @ResponseBody String domobNotify(@RequestParam("udid") String mac, @RequestParam("app") String appId,  
			@RequestParam("source") String source, @RequestParam("returnFormat") String returnFormat) {
		JSONObject responseJson = new JSONObject();
		try {
			logger.info("多盟广告点击记录 start mac="+mac+";appId="+appId+";source="+source+";returnFormat="+returnFormat);
			List<AdvertiseInfo> list = getAdvertiseInfoListByAppid(mac, appId, source);
			if (list==null||list.size()==0) {
				//保存记录
				saveAdvertiseInfoByAppid(mac, appId, source);
				return responseSuccess(responseJson, true, "通知成功");
			} else {
				return responseSuccess(responseJson, false, "重复记录");
			}
		} catch (Exception e) {
			logger.error("多盟广告点击记录发生异常", e);
		}
		//logger.info("多盟广告点击记录 end mac="+mac+";appId="+appId+";source="+source+";returnFormat="+returnFormat);
		return responseSuccess(responseJson, false, "通知失败");
	}
	
	/**
	 * 点乐广告
	 * @param mac
	 * @param advertiseId
	 * @param source
	 * @return
	 */
	/*@RequestMapping(value = "/notify", method = RequestMethod.GET)
	public @ResponseBody String notify(@RequestParam("mac") String mac, @RequestParam("ad_id") String advertiseId,
			@RequestParam("source") String source) {
		JSONObject responseJson = new JSONObject();
		try {
			logger.info("点乐广告点击记录 start mac="+mac+";advertiseId="+advertiseId+";source="+source);
			List<AdvertiseInfo> list = getAdvertiseInfoListByAdvertiseId(mac, advertiseId, source);
			if (list==null||list.size()==0) {
				//保存记录
				saveAdvertiseInfoByAdvertiseId(mac, advertiseId, source);
				return responseErrorCode(responseJson, "0000", "通知成功");
			} else {
				return responseErrorCode(responseJson, "9999", "重复记录");
			}
		} catch (Exception e) {
			logger.error("点乐广告点击记录发生异常", e);
		}
		//logger.info("点乐广告点击记录 end mac="+mac+";advertiseId="+advertiseId+";source="+source+",result="+responseJson.toString());
		return responseErrorCode(responseJson, "9999", "通知失败");
	}*/
	
	/**
	 * 请求响应(success)
	 * @param responseJson
	 * @param success
	 * @param message
	 * @return
	 */
	private String responseSuccess(JSONObject responseJson, boolean success, String message) {
		responseJson.put("success", success);
		responseJson.put("message", message);
		return responseJson.toString();
	}
	
	/**
	 * 请求响应(errorCode)
	 * @param responseJson
	 * @param errorCode
	 * @param message
	 * @return
	 */
	/*private String responseErrorCode(JSONObject responseJson, String errorCode, String message) {
		responseJson.put("error_code", errorCode);
		responseJson.put("message", message);
		return responseJson.toString();
	}*/
	
	/**
	 * 根据appId查询广告记录
	 * @param mac
	 * @param appId
	 * @param source
	 * @return
	 */
	private List<AdvertiseInfo> getAdvertiseInfoListByAppid(String mac, String appId, String source) {
		StringBuilder builder = new StringBuilder(" where");
		List<Object> params = new ArrayList<Object>();
		
		builder.append(" o.mac=? and");
		params.add(mac);
		
		builder.append(" o.appid=? and");
		params.add(appId);
		
		builder.append(" o.source=? ");
		params.add(source);
		
		List<AdvertiseInfo> list = AdvertiseInfo.getList(builder.toString(), "", params);
		return list;
	}
	
	/**
	 * 根据advertiseId查询广告记录
	 * @param mac
	 * @param advertiseId
	 * @param source
	 * @return
	 */
	/*private List<AdvertiseInfo> getAdvertiseInfoListByAdvertiseId(String mac, String advertiseId, String source) {
		StringBuilder builder = new StringBuilder(" where");
		List<Object> params = new ArrayList<Object>();
		
		builder.append(" o.mac=? and");
		params.add(mac);
		
		builder.append(" o.advertiseid=? and");
		params.add(advertiseId);
		
		builder.append(" o.source=? ");
		params.add(source);
		
		List<AdvertiseInfo> list = AdvertiseInfo.getList(builder.toString(), "", params);
		return list;
	}*/
	
	/**
	 * 根据drkey查询广告记录
	 * @param mac
	 * @param drkey
	 * @param source
	 * @return
	 */
	private List<AdvertiseInfo> getAdvertiseInfoListByDrkey(String mac, String drkey, String source) {
		StringBuilder builder = new StringBuilder(" where");
		List<Object> params = new ArrayList<Object>();
		
		builder.append(" o.mac=? and");
		params.add(mac);
		
		builder.append(" o.drkey=? and");
		params.add(drkey);
		
		builder.append(" o.source=? ");
		params.add(source);
		
		List<AdvertiseInfo> list = AdvertiseInfo.getList(builder.toString(), "", params);
		return list;
	}
	
	/**
	 * 根据appId保存广告记录
	 * @param mac
	 * @param appId
	 * @param source
	 */
	private void saveAdvertiseInfoByAppid(String mac, String appId, String source) {
		AdvertiseInfo advertiseInfo = new AdvertiseInfo();
		advertiseInfo.setMac(mac);
		advertiseInfo.setAppid(appId);
		advertiseInfo.setSource(source);
		advertiseInfo.setCreatetime(new Date());
		advertiseInfo.setUpdatetime(new Date());
		advertiseInfo.setState("1");
		advertiseInfo.persist();
	}
	
	/**
	 * 根据advertiseId保存广告记录
	 * @param mac
	 * @param advertiseId
	 * @param source
	 */
	/*private void saveAdvertiseInfoByAdvertiseId(String mac, String advertiseId, String source) {
		AdvertiseInfo advertiseInfo = new AdvertiseInfo();
		advertiseInfo.setMac(mac);
		advertiseInfo.setAdvertiseid(advertiseId);
		advertiseInfo.setSource(source);
		advertiseInfo.setCreatetime(new Date());
		advertiseInfo.setUpdatetime(new Date());
		advertiseInfo.setState("1");
		advertiseInfo.persist();
	}*/
	
	/**
	 * 根据drkey保存广告记录
	 * @param mac
	 * @param drkey
	 * @param source
	 */
	private void saveAdvertiseInfoByDrkey(String mac, String drkey, String source) {
		AdvertiseInfo advertiseInfo = new AdvertiseInfo();
		advertiseInfo.setMac(mac);
		advertiseInfo.setDrkey(drkey);
		advertiseInfo.setSource(source);
		advertiseInfo.setCreatetime(new Date());
		advertiseInfo.setUpdatetime(new Date());
		advertiseInfo.setState("1");
		advertiseInfo.persist();
	}
	
}
