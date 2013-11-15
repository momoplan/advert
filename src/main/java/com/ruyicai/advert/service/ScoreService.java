package com.ruyicai.advert.service;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.center.ScoreWall;
import com.ruyicai.advert.controller.ResponseDataScore;

@Service
public class ScoreService {

	private Logger logger = Logger.getLogger(ScoreService.class);
	
	@Autowired
	private AdvertManager advertManager;
	
	/**
	 * 力美加积分
	 * @param aduid
	 * @param uid
	 * @param aid
	 * @param point
	 * @param source
	 * @param sign
	 * @param timestamp
	 * @param idfa
	 * @return
	 */
	public ResponseDataScore limeiAddScore(String ip, String aduid, String uid, String aid, 
			String point, String source, String sign, String timestamp, String idfa) {
		logger.info("力美积分墙加积分通知 start aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source
				+";sign="+sign+";timestamp="+timestamp+";idfa="+idfa+";ip="+ip);
		//验证用户名是否为空
		if (StringUtils.isBlank(aid)) {
			logger.error("力美积分墙加积分,aid为空  aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source);
			return new ResponseDataScore("500", "aid为空");
		}
		ScoreWall scoreWall = advertManager.getScoreWall("limei");
		if (scoreWall==null) {
			logger.error("力美积分墙加积分,未对接  aduid="+aduid+";uid="+uid+";aid="+aid+";point="+point+";source="+source);
			return new ResponseDataScore("500", "未对接");
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("ip", ip);
		param.put("aduid", aduid);
		param.put("aid", aid);
		param.put("point", point);
		param.put("source", source);
		param.put("sign", sign);
		param.put("timestamp", timestamp);
		param.put("idfa", idfa);
		Map<String, Object> resultMap = scoreWall.addScore(param);
		return new ResponseDataScore((String)resultMap.get("success"), (String)resultMap.get("message"));
	}
	
}
