package com.ruyicai.advert.util;

import java.util.Date;
import java.util.List;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.consts.AdvertiseSource;
import com.ruyicai.advert.domain.AdvertiseInfo;

/**
 * 广告公共类
 * @author Administrator
 *
 */
@Service
public class AdvertiseUtil {

	private Logger logger = Logger.getLogger(AdvertiseUtil.class);
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	public AdvertiseInfo getValidAdvertiseInfo(String mac, String source) {
		List<AdvertiseInfo> advertiseInfos = AdvertiseInfo.getListByMacSource(mac, source);
		if (advertiseInfos!=null && advertiseInfos.size()>0) {
			for (AdvertiseInfo advertiseInfo : advertiseInfos) {
				String state = advertiseInfo.getState();
				if (StringUtils.equals(state, "0")) { //已经激活过
					return advertiseInfo;
				}
			}
			return advertiseInfos.get(0);
		}
		return null;
	}
	
	public AdvertiseInfo getValidAdvertiseInfo(String mac) {
		List<AdvertiseInfo> advertiseInfos = AdvertiseInfo.getListByMac(mac);
		if (advertiseInfos!=null && advertiseInfos.size()>0) {
			for (AdvertiseInfo advertiseInfo : advertiseInfos) {
				String state = advertiseInfo.getState();
				if (StringUtils.equals(state, "0")) { //已经激活过
					return advertiseInfo;
				}
			}
			return advertiseInfos.get(0);
		}
		return null;
	}
	
	/**
	 * 广告通知第三方
	 * @param coopId
	 */
	public void notifyThirdParty(AdvertiseInfo advertiseInfo) {
		try {
			if (advertiseInfo!=null&&StringUtils.equals(advertiseInfo.getState(), "1")) { //未通知过第三方
				String source = advertiseInfo.getSource(); //渠道
				if (StringUtils.equals(source, AdvertiseSource.limei.value())) { //力美
					limeiNotify(advertiseInfo);
				} else if (StringUtils.equals(source, AdvertiseSource.dianRu.value())) { //点入
					dianruNotify(advertiseInfo);
				} else if (StringUtils.equals(source, AdvertiseSource.duoMeng.value())) { //多盟
					domobNotify(advertiseInfo);
				}
			}
		} catch (Exception e) {
			logger.error("广告通知第三方发生异常", e);
		}
	}
	
	/**
	 * 力美通知
	 * @param advertiseInfo
	 */
	public void limeiNotify(AdvertiseInfo advertiseInfo) {
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
	
	/**
	 * 点入通知
	 * @param advertiseInfo
	 */
	public void dianruNotify(AdvertiseInfo advertiseInfo) {
		String drkey = advertiseInfo.getDrkey(); 
		
		String result = "";
		int requestCount = 1;
		while (StringUtil.isEmpty(result) && requestCount<4) {
			String url = propertiesUtil.getDianru_notifyUrl()+"?drkey="+drkey+"&isvalidactive=true";
			result = HttpUtil.sendRequestByGet(url, true);
			logger.info("广告通知点入返回:"+result+";drkey="+drkey+";requestCount="+requestCount);
			if (!StringUtil.isEmpty(result)) {
				JSONObject fromObject = JSONObject.fromObject(result);
				String status = fromObject.getString("status");
				if (status!=null&&status.equals("1")) { //成功
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
	
	/**
	 * 多盟通知
	 * @param advertiseInfo
	 */
	public void domobNotify(AdvertiseInfo advertiseInfo) {
		String appId = advertiseInfo.getAppid();
		String mac = advertiseInfo.getMac();
		
		String result = "";
		int requestCount = 1;
		while (StringUtil.isEmpty(result) && requestCount<4) {
			String url = propertiesUtil.getDomob_notifyUrl()+"?appId="+appId+"&udid="+mac+"&returnFormat=1";
			result = HttpUtil.sendRequestByGet(url, true);
			logger.info("广告通知多盟返回:"+result+";mac="+mac+";requestCount="+requestCount);
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
	
	/**
	 * 更新AdvertiseInfo的状态
	 * @param advertiseInfo
	 */
	public void updateAdvertiseInfoState(AdvertiseInfo advertiseInfo) {
		advertiseInfo.setState("0");
		advertiseInfo.setUpdatetime(new Date());
		advertiseInfo.merge();
	}
	
}
