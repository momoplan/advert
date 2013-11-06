package com.ruyicai.advert.center;

import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.advert.util.PropertiesUtil;
import com.ruyicai.advert.util.StringUtil;

@Component("miidi")
public class Miidi extends AbstractScoreWall {

	private Logger logger = Logger.getLogger(Miidi.class);
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	@Override
	public void init() {
		
	}
	
	@Override
	public Map<String, Object> receiveAdvertise(Map<String, String> param) {
		String ip = param.get("ip");
		String mac = param.get("mac");
		String appId = param.get("appId");
		String source = param.get("source");
		logger.info("米迪广告点击记录 start mac="+mac+";appId="+appId+";source="+source+";ip="+ip);
		//验证ip
		/*boolean verfyIp = VerifyUtil.verfyIp(ip, propertiesUtil.getDianru_ip());
		if (!verfyIp) {
			logger.error("米迪广告点击记录,ip不合法 mac="+mac+";appId="+appId+";source="+source+";ip="+ip);
			return new ResponseData(false, "ip不合法");
		}*/
		//验证是否已激活
		boolean verifyActivate = verifyActivate(mac);
		if (!verifyActivate) {
			logger.error("米迪广告点击记录,已被激活 mac="+mac);
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
	
	@Override
	public void notifyActivate(AdvertiseInfo advertiseInfo) {
		String adMac = advertiseInfo.getMac(); //广告传过来的mac地址
		String appId = advertiseInfo.getAppid(); //app标识
		
		String result = "";
		int requestCount = 1;
		while (StringUtil.isEmpty(result) && requestCount<4) {
			String url = propertiesUtil.getMiidi_notifyUrl()+"?appid="+appId+"&mac="+adMac;
			result = HttpUtil.sendRequestByGet(url, true);
			logger.info("广告通知米迪返回:"+result+";mac="+adMac+";requestCount="+requestCount);
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
		return null;
	}

}
