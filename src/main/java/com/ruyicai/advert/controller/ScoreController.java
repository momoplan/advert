package com.ruyicai.advert.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
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
	public @ResponseBody String limeiNotify(@RequestParam("aduid") String aduid, @RequestParam("uid") String uid,
			@RequestParam("aid") String aid, @RequestParam("point") String point, @RequestParam("source") String source,
			@RequestParam("sign") String sign, @RequestParam("timestamp") String timestamp) {
		JSONObject responseJson = new JSONObject();
		try {
			logger.info("力美积分墙加积分通知 start aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source
					+";sign="+sign+";timestamp="+timestamp);
			//验证sign
			boolean verfySign = verfySign(aduid, uid, aid, point, source, sign, timestamp);
			if (!verfySign) {
				logger.error("签名错误,aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source);
				responseJson.put("code", "500");
				responseJson.put("message", "签名错误");
				return responseJson.toString();
			}
			//将积分(231.0)转成不带小数点
			NumberFormat nf = NumberFormat.getInstance();
			point = nf.format(new BigDecimal(point));
			//记录通知信息
			ScoreInfo scoreInfo = recordScoreInfo(aduid, uid, aid, point, source, sign, timestamp);
			//查询用户编号
			JSONObject userObject = getUserNoByUserName(aid);
			String userNo = userObject.getString("userNo");
			String channel = userObject.getString("channel");
			if (StringUtils.isBlank(userNo)) {
				responseJson.put("code", "500");
				responseJson.put("message", "用户名不存在");
				return responseJson.toString();
			}
			//送彩金
			String errorCode = presentDividend(userNo, point, channel);
			if (StringUtils.equals(errorCode, "0")) { //赠送成功
				//更新积分记录的状态
				updateScoreInfo(scoreInfo, userNo);
				
				responseJson.put("code", "200");
				responseJson.put("message", "赠送成功");
				return responseJson.toString();
			}
		} catch (Exception e) {
			logger.error("力美积分墙加积分通知发生异常", e);
		}
		responseJson.put("code", "500");
		responseJson.put("message", "赠送失败");
		return responseJson.toString();
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
	 * 根据用户名查询用户编号
	 * @param userName
	 * @return
	 */
	private JSONObject getUserNoByUserName(String userName) {
		JSONObject resultObject = new JSONObject();
		try {
			String result = lotteryService.queryUsersByUserName(userName);
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
		} catch (UnsupportedEncodingException e) {
			logger.error("根据用户名查询用户编号发生异常", e);
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
	private String presentDividend(String userNo, String point, String channel) {
		String result = lotteryService.presentDividend(userNo, point, channel);
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
			scoreInfo.setUserno(userNo);
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
	
}
