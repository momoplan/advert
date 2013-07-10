package com.ruyicai.advert.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.ruyicai.advert.consts.Constants;
import com.ruyicai.advert.domain.ScoreInfo;
import com.ruyicai.advert.service.LotteryService;
import com.ruyicai.advert.util.PropertiesUtil;
import com.ruyicai.advert.util.Tools;

/**
 * 积分墙积分Controller
 * @author Administrator
 *
 */
@RequestMapping("/score")
@Controller
public class ScoreController {

	private Logger logger = Logger.getLogger(AdvertiseController.class);
	
	@Autowired
	private LotteryService lotteryService;
	
	@Autowired
	private PropertiesUtil propertiesUtil;

	/**
	 * 力美积分墙加积分通知
	 * @param aduid
	 * @param uid
	 * @param aid
	 * @param point
	 * @param source
	 * @param sign
	 * @param timestamp
	 * @return
	 */
	@RequestMapping(value = "/limeiNotify", method = RequestMethod.GET)
	public @ResponseBody String limeiNotify(HttpServletRequest request, @RequestParam("aduid") String aduid, 
			@RequestParam("uid") String uid, @RequestParam("aid") String aid, @RequestParam("point") String point, 
			@RequestParam("source") String source, @RequestParam("sign") String sign, @RequestParam("timestamp") String timestamp) {
		JSONObject responseJson = new JSONObject();
		try {
			String ip = request.getHeader("X-Forwarded-For");
			logger.info("力美积分墙加积分通知 start aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source
					+";sign="+sign+";timestamp="+timestamp+";ip="+ip);
			//验证ip
			if (!verfyIp(ip)) {
				logger.error("ip不合法,aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source+";ip="+ip);
				return response(responseJson, "500", "ip不合法");
			}
			//验证用户名是否为空
			if (StringUtils.isBlank(aid)) {
				logger.error("aid为空,aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source);
				return response(responseJson, "500", "aid为空");
			}
			//验证sign
			boolean verfySign = verfySign(aduid, uid, aid, point, source, sign, timestamp);
			if (!verfySign) {
				logger.error("签名错误,aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source);
				return response(responseJson, "500", "签名错误");
			}
			//判断重复请求
			if (!verifyRepeatRequest(sign)) {
				logger.error("重复请求,aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source);
				return response(responseJson, "500", "重复请求");
			}
			//将积分(231.0)转成不带小数点
			NumberFormat nf = NumberFormat.getInstance();
			point = nf.format(new BigDecimal(point));
			//记录通知信息
			ScoreInfo scoreInfo = recordScoreInfo(aduid, uid, aid, point, source, sign, timestamp);
			//查询用户编号
			JSONObject userObject = getUserByUserNo(aid);
			String userNo = userObject.getString("userNo");
			String channel = userObject.getString("channel");
			if (StringUtils.isBlank(userNo)) {
				return response(responseJson, "500", "用户不存在");
			}
			//送彩金
			String errorCode = presentDividend(userNo, point, channel, "力美免费彩金");
			if (StringUtils.equals(errorCode, "0")) { //赠送成功
				//更新积分记录的状态
				updateScoreInfo(scoreInfo, userNo);
				return response(responseJson, "200", "赠送成功");
			}
		} catch (Exception e) {
			logger.error("力美积分墙加积分通知发生异常", e);
		}
		return response(responseJson, "500", "赠送失败");
	}
	
	/**
	 * 记录通知信息
	 * @param aduid
	 * @param uid
	 * @param aid
	 * @param point
	 * @param source
	 * @param sign
	 * @param timestamp
	 */
	private ScoreInfo recordScoreInfo(String aduid, String uid, String aid, String point, String source, String sign, String timestamp) {
		ScoreInfo scoreInfo = new ScoreInfo();
		scoreInfo.setAduid(aduid);
		scoreInfo.setUid(uid);
		scoreInfo.setAid(aid);
		scoreInfo.setPoint(new BigDecimal(point));
		scoreInfo.setSource(source);
		scoreInfo.setSign(sign);
		scoreInfo.setTimestamp(timestamp);
		scoreInfo.setCreatetime(new Date());
		scoreInfo.setState("1");
		scoreInfo.persist();
		return scoreInfo;
	}
	
	/**
	 * 根据用户编号查询用户
	 * @param userNo
	 * @return
	 */
	private JSONObject getUserByUserNo(String userNo) {
		JSONObject resultObject = new JSONObject();
		try {
			String result = lotteryService.getUserByUserNo(userNo);
			if (StringUtils.isBlank(result)) {
				resultObject.put("userNo", "");
				resultObject.put("channel", "");
				return resultObject;
			}
			JSONObject fromObject = JSONObject.fromObject(result);
			if (fromObject==null) {
				resultObject.put("userNo", "");
				resultObject.put("channel", "");
				return resultObject;
			}
			String errorCode = fromObject.getString("errorCode");
			if (!StringUtils.equals(errorCode, "0")) {
				resultObject.put("userNo", "");
				resultObject.put("channel", "");
				return resultObject;
			}
			JSONObject valueJsonObject = fromObject.getJSONObject("value");
			resultObject.put("userNo", valueJsonObject.getString("userno"));
			resultObject.put("channel", valueJsonObject.getString("channel"));
			return resultObject;
		} catch (Exception e) {
			logger.error("根据用户编号查询用户发生异常", e);
		}
		resultObject.put("userNo", "");
		resultObject.put("channel", "");
		return resultObject;
	}
	
	/**
	 * 赠送彩金
	 * @param userNo
	 * @param point
	 * @return
	 */
	private String presentDividend(String userNo, String point, String channel, String memo) {
		String result = lotteryService.presentDividend(userNo, point, channel, memo);
		if (StringUtils.isBlank(result)) {
			return "";
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject==null) {
			return "";
		}
		return fromObject.getString("errorCode");
	}
	
	/**
	 * 更新积分记录
	 * @param scoreInfo
	 * @param userNo
	 */
	private void updateScoreInfo(ScoreInfo scoreInfo, String userNo) {
		try {
			scoreInfo.setState("0");
			scoreInfo.setUpdatetime(new Date());
			scoreInfo.merge();
		} catch (Exception e) {
			logger.error("更新积分记录发生异常", e);
		}
	}
	
	/**
	 * 验证签名
	 * @param aduid
	 * @param uid
	 * @param aid
	 * @param point
	 * @param source
	 * @param sign
	 * @param timestamp
	 * @return
	 */
	private boolean verfySign(String aduid, String uid, String aid, String point, String source, String sign, String timestamp) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("aduid").append(aduid);
			builder.append("aid").append(aid);
			builder.append("point").append(point);
			builder.append("source").append(source);
			builder.append("timestamp").append(timestamp);
			builder.append("uid").append(uid);
			String hmac = Tools.hmac(builder.toString(), Constants.limeiSecretKey);
			if (StringUtils.equals(hmac, sign)) {
				return true;
			}
		} catch (Exception e) {
			logger.error("验证签名发生异常", e);
		}
		return false;
	}
	
	/**
	 * 验证ip是否合法
	 * @param ip
	 * @return
	 */
	private boolean verfyIp(String ip) {
		if (StringUtils.isBlank(ip)) {
			return false;
		}
		String[] ips = StringUtils.split(ip, ",");
		String[] limeiIps = StringUtils.split(propertiesUtil.getLimei_ip(), ",");
		List<String> limeiIpList = Arrays.asList(limeiIps);
		for (String p : ips) {
			if (StringUtils.isNotBlank(p)&&limeiIpList.contains(p.trim())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 验证重复请求
	 * @param sign
	 * @return
	 */
	private boolean verifyRepeatRequest(String sign) {
		try {
			StringBuilder builder = new StringBuilder(" where");
			List<Object> params = new ArrayList<Object>();
			
			builder.append(" o.sign=? ");
			params.add(sign);
			
			List<ScoreInfo> list = ScoreInfo.getList(builder.toString(), "", params);
			if (list!=null&&list.size()>0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("积分墙积分验证重复请求发生异常", e);
		}
		return false;
	}
	
	/**
	 * 请求响应
	 * @param responseJson
	 * @param errorCode
	 * @param message
	 */
	private String response(JSONObject responseJson, String errorCode, String message) {
		responseJson.put("code", errorCode);
		responseJson.put("message", message);
		return responseJson.toString();
	}
	
}
