package com.ruyicai.advert.center;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruyicai.advert.consts.Constants;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.domain.ScoreBind;
import com.ruyicai.advert.domain.ScoreInfo;
import com.ruyicai.advert.service.LotteryService;
import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.advert.util.PropertiesUtil;
import com.ruyicai.advert.util.StringUtil;
import com.ruyicai.advert.util.Tools;

@Component("limei")
public class Limei extends AbstractScoreWall {

	private Logger logger = Logger.getLogger(Limei.class);
	
	@Autowired
	private LotteryService lotteryService;
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	public void init() {
		this.setIp(propertiesUtil.getLimei_ip());
	}
	
	@Override
	public Map<String, Object> receiveAdvertise(Map<String, String> param) {
		String ip = param.get("ip");
		String mac = param.get("mac");
		String appId = param.get("appId");
		String source = param.get("source");
		//验证ip
		boolean verfyIp = verfyIp(ip, getIp());
		if (!verfyIp) {
			logger.error("力美广告点击记录,ip不合法 mac="+mac+";appId="+appId+";source="+source+";ip="+ip);
			return response(false, "ip不合法");
		}
		//转换mac
		mac = transferMac(mac);
		//验证是否已激活
		boolean verifyActivate = verifyActivate(mac);
		if (!verifyActivate) {
			logger.error("力美广告点击记录,已被激活 mac="+mac);
			return response(false, "已被激活");
		}
		List<AdvertiseInfo> list = AdvertiseInfo.getListByMacSourceAppid(mac, source, appId);
		if (list==null||list.size()==0) {
			//保存记录
			saveAdvertiseInfoByAppid(mac, appId, source);
			return response(true, "通知成功");
		} else {
			return response(false, "重复记录");
		}
	}
	
	private String transferMac(String mac) {
		if (StringUtils.indexOf(mac, ":")>-1 || StringUtils.indexOf(mac, "-")>-1) {
			return mac;
		} else {
			if (mac.length()==12) { //mac(不带":")
				mac = StringUtils.join(StringUtil.getStringArrayFromString(mac, 2), ":");
			}
		}
		return mac;
	}
	
	@Override
	public void notifyActivate(AdvertiseInfo advertiseInfo) {
		String adMac = advertiseInfo.getMac(); //广告传过来的mac地址
		String appId = advertiseInfo.getAppid(); //app标识
		//转换mac(不加密,不带分隔符,大写)
		adMac = adMac.replaceAll(":", "").toUpperCase();
		
		String result = "";
		int requestCount = 1;
		while (StringUtil.isEmpty(result) && requestCount<4) {
			String url = propertiesUtil.getLimei_notifyUrl()+"?appId="+appId+"&udid="+adMac+"&returnFormat=1";
			result = HttpUtil.sendRequestByGet(url, true);
			logger.info("广告通知力美返回:"+result+";mac="+adMac+";requestCount="+requestCount);
			if (!StringUtil.isEmpty(result)) {
				JSONObject fromObject = JSONObject.fromObject(result);
				boolean success = fromObject.getBoolean("success");
				if (success) { //成功
					updateAdvertiseInfoState(advertiseInfo); //更新AdvertiseInfo表的状态
				}
			} else {
				requestCount++;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public Map<String, Object> addScore(Map<String, String> param) {
		String ip = param.get("ip");
		String aduid = param.get("aduid");
		String uid = param.get("uid");
		String aid = param.get("aid");
		String point = param.get("point");
		String source = param.get("source");
		String sign = param.get("sign");
		String timestamp = param.get("timestamp");
		String idfa = param.get("idfa");
		//验证ip
		boolean verfyIp = verfyIp(ip, getIp());
		if (!verfyIp) {
			logger.error("力美积分墙加积分,ip不合法  aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source+";ip="+ip);
			return response("500", "ip不合法");
		}
		//验证sign
		boolean verfySign = verifySign(aduid, uid, aid, point, source, sign, timestamp, idfa);
		if (!verfySign) {
			logger.error("力美积分墙加积分,签名错误  aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source);
			return response("500", "签名错误");
		}
		//判断重复请求
		if (!verifyRepeatRequest(sign)) {
			logger.error("力美积分墙加积分,重复请求  aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source);
			return response("500", "重复请求");
		}
		//查询用户编号
		JSONObject userObject = getUserByUserNo(aid);
		String userNo = userObject.getString("userNo");
		if (StringUtils.isBlank(userNo)) {
			return response("500", "用户不存在");
		}
		//判断是否绑定手机号
		String mobileId = userObject.getString("mobileId").trim();
		if (StringUtils.isBlank(mobileId) || StringUtils.equals(mobileId, "null")) {
			logger.error("力美积分墙加积分,未绑定手机号  aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source);
			return response("500", "未绑定手机号");
		}
		//判断是否是作弊用户
		if (!verifyCheat(mobileId, uid, idfa)) {
			logger.error("力美积分墙加积分,用户作弊  aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source);
			return response("500", "用户作弊");
		}
		//将积分(231.0)转成不带小数点,1242会转成(1,1242)
		NumberFormat nf = NumberFormat.getInstance();
		point = nf.format(new BigDecimal(point)).replaceAll(",", "");
		//记录通知信息
		ScoreInfo scoreInfo = recordScoreInfo(aduid, uid, aid, point, source, sign, timestamp);
		//送彩金
		String channel = userObject.getString("channel");
		String errorCode = presentDividend(userNo, point, channel, "力美免费彩金");
		if (StringUtils.equals(errorCode, "0")) { //赠送成功
			updateScoreInfo(scoreInfo, userNo); //更新积分记录的状态
			return response("200", "赠送成功");
		}
		return response("500", "赠送失败");
	}
	
	private boolean verifySign(String aduid, String uid, String aid, String point, String source, String sign, String timestamp,
			String idfa) {
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("aduid").append(aduid);
			builder.append("aid").append(aid);
			builder.append("idfa").append(idfa);
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
	
	private boolean verifyRepeatRequest(String sign) {
		List<ScoreInfo> list = ScoreInfo.findBySign(sign);
		if (list==null||list.size()<=0) {
			return true;
		}
		return false;
	}
	
	private boolean verifyCheat(String mobileId, String mac, String idfa) {
		boolean right = false;
		//ios7及以上版本用idfa
		if (StringUtils.isNotBlank(idfa) && (StringUtils.equals(mac, "020000000000")
				||StringUtils.isBlank(mac))) {
			mac = idfa;
		}
		List<String> list = ScoreBind.findByMobileid(mobileId);
		if (list==null || list.size()<=0) { //没有绑定记录
			right = true;
		} else {
			if (list.size()>=2 && !list.contains(mac)) {
				right = false;
			} else {
				right = true;
			}
		}
		if (right&&(list==null||!list.contains(mac))) {
			new Thread(new SaveScoreBindThread(mac, mobileId)).start();
		}
		return right;
	}
	
	private final class SaveScoreBindThread implements Runnable {
		private String mac;
		private String mobileId;
		
		public SaveScoreBindThread(String mac, String mobileId) {
			this.mac = mac;
			this.mobileId = mobileId;
		}

		@Override
		public void run() {
			try {
				ScoreBind scoreBind = new ScoreBind();
				scoreBind.setMac(mac);
				scoreBind.setMobileid(mobileId);
				scoreBind.setCreatetime(new Date());
				scoreBind.persist();
			} catch (Exception e) {
				logger.error("保存ScoreBind发生异常,mac="+mac+",mobileId="+mobileId, e);
			}
		}
		
	}
	
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
	
	private JSONObject getUserByUserNo(String userNo) {
		JSONObject resultObject = new JSONObject();
		try {
			resultObject.put("userNo", "");
			String result = lotteryService.getUserByUserNo(userNo);
			if (StringUtils.isBlank(result)) {
				return resultObject;
			}
			JSONObject fromObject = JSONObject.fromObject(result);
			if (fromObject==null) {
				return resultObject;
			}
			String errorCode = fromObject.getString("errorCode");
			if (!StringUtils.equals(errorCode, "0")) {
				return resultObject;
			}
			JSONObject valueJsonObject = fromObject.getJSONObject("value");
			resultObject.put("userNo", valueJsonObject.getString("userno"));
			resultObject.put("mobileId", valueJsonObject.getString("mobileid"));
			resultObject.put("channel", valueJsonObject.getString("channel"));
			return resultObject;
		} catch (Exception e) {
			logger.error("根据用户编号查询用户发生异常", e);
		}
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
	
	private void updateScoreInfo(ScoreInfo scoreInfo, String userNo) {
		try {
			scoreInfo.setState("0");
			scoreInfo.setUpdatetime(new Date());
			scoreInfo.merge();
		} catch (Exception e) {
			logger.error("更新积分记录发生异常", e);
		}
	}

}
