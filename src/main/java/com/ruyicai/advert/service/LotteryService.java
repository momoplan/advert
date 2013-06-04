package com.ruyicai.advert.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.consts.Constants;
import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.advert.util.PropertiesUtil;

@Service
public class LotteryService {

	private Logger logger = LoggerFactory.getLogger(LotteryService.class);
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	/**
	 * 根据用户名查询用户
	 * 
	 * @param mobileid
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String queryUsersByUserName(String userName) throws UnsupportedEncodingException {
		StringBuffer paramStr = new StringBuffer();
		paramStr.append("userName=" + URLEncoder.encode(userName, "UTF-8"));

		String url = propertiesUtil.getLotteryUrl() + "tuserinfoes?json&find=ByUserName";
		String result = HttpUtil.sendRequestByPost(url, paramStr.toString(), true);
		//logger.info("根据用户名查询用户返回:"+result+",paramStr:"+paramStr.toString());
		return result;
	}
	
	/**
	 * 赠送彩金
	 * @param userNo
	 * @param amount
	 * @return
	 */
	public String presentDividend(String userNo, String amount, String channel) {
		StringBuffer paramStr = new StringBuffer();
		paramStr.append("userno=" + userNo);
		paramStr.append("&amt=" + amount);
		paramStr.append("&accesstype=" + Constants.accessType);
		paramStr.append("&subchannel=" + Constants.subChannel);
		paramStr.append("&channel=" + channel);
		
		String url = propertiesUtil.getLotteryUrl() + "taccounts/doDirectChargeProcess";
		String result = HttpUtil.sendRequestByPost(url, paramStr.toString(), true);
		logger.info("赠送彩金返回:"+result+",userNo:"+userNo+";amount:"+amount);
		return result;
	}
	
}
