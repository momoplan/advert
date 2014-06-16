package com.ruyicai.advert.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.domain.TempInfo;
import com.ruyicai.advert.domain.TregisterInfo;
import com.ruyicai.advert.domain.UserInf;
import com.ruyicai.advert.service.back.LotteryService;

@Service
public class CommonService {

	private Logger logger = Logger.getLogger(CommonService.class);
	
	@Autowired
	private LotteryService lotteryService;
	
	/**
	 * 统计激活和注册数
	 * @param type(1:激活;2:注册)
	 */
	public void parseMac(String type) {
		File file = new File("E:/log/mac.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			//一次读入一行，直到读入null为文件结束
			int line = 0;
			while ((tempString = reader.readLine()) != null) {
				line++;
				System.out.println("line:"+line+",mac="+tempString);
				if (StringUtils.isBlank(tempString)) {
					continue;
				}
				if (StringUtils.equals(type, "1")) { //激活
					parseActive(tempString);
				} else if (StringUtils.equals(type, "2")) { //注册
					parseRegister(tempString);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private void parseActive(String mac) {
		try {
			List<UserInf> list = UserInf.getListByMacPlatform(mac, "iPhone");
			if (list==null||list.size()<=0) {
				return;
			}
			if (list.size()>1) {
				logger.info("有多条激活记录,mac="+mac);
			}
			UserInf userInf = list.get(0);
			String channel = userInf.getChannel();
			if (!StringUtils.equals(channel, "887")) {
				return;
			}
			Date createtime = userInf.getCreatetime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String format = sdf.format(createtime);
			if (StringUtils.equals(format, "2014-04-19")||StringUtils.equals(format, "2014-04-20")
					||StringUtils.equals(format, "2014-04-21")) {
				TempInfo tempInfo = new TempInfo();
				tempInfo.setType("1");
				tempInfo.setMac(mac);
				tempInfo.persist();
			}
		} catch (Exception e) {
			logger.error("统计激活发生异常,mac="+mac, e);
		}
	}
	
	private void parseRegister(String mac) {
		try {
			List<TregisterInfo> list = TregisterInfo.findByImeiPlatform(mac, "iPhone");
			if (list==null||list.size()<=0) {
				return;
			}
			for (TregisterInfo tregisterInfo : list) {
				String channel = tregisterInfo.getChannel();
				if (!StringUtils.equals(channel, "887")) {
					continue;
				}
				Date createtime = tregisterInfo.getCreatetime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String format = sdf.format(createtime);
				if (StringUtils.equals(format, "2014-04-19")||StringUtils.equals(format, "2014-04-20")
						||StringUtils.equals(format, "2014-04-21")) {
					TempInfo tempInfo = new TempInfo();
					tempInfo.setType("2");
					tempInfo.setMac(mac);
					tempInfo.setUserno(tregisterInfo.getUserno());
					tempInfo.persist();
				}
			}
		} catch (Exception e) {
			logger.error("统计注册发生异常,mac="+mac, e);
		}
	}
	
	/**
	 * 根据用户编号获得用户信息
	 * @param userno
	 * @return
	 */
	public JSONObject getUserinfoByUserno(String userno) {
		String result = lotteryService.getUserByUserNo(userno);
		if (StringUtils.isBlank(result)) {
			return null;
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject==null) {
			return null;
		}
		String errorCode = fromObject.getString("errorCode");
		if (StringUtils.equals(errorCode, "0")) {
			return fromObject.getJSONObject("value");
		}
		return null;
	}
	
	/**
	 * 赠送彩金
	 * @param userNo
	 * @param point
	 * @return
	 */
	public String presentDividend(String userNo, String point, String channel, String memo) {
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

}
