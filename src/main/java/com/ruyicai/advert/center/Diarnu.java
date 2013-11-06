package com.ruyicai.advert.center;

import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.advert.util.PropertiesUtil;
import com.ruyicai.advert.util.StringUtil;

@Component("diarnu")
public class Diarnu extends AbstractScoreWall {

	private Logger logger = Logger.getLogger(Diarnu.class);
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	public void init() {
		this.setIp(propertiesUtil.getDianru_ip());
	}
	
	@Override
	public Map<String, Object> receiveAdvertise(Map<String, String> param) {
		String ip = param.get("ip");
		String drkey = param.get("drkey");
		String source = param.get("source");
		logger.info("点入广告点击记录 start drkey="+drkey+";source="+source+";ip="+ip);
		//验证ip
		boolean verfyIp = verfyIp(ip, getIp());
		if (!verfyIp) {
			logger.error("点入广告点击记录,ip不合法 drkey="+drkey+";source="+source+";ip="+ip);
			return response(false, "ip不合法");
		}
		String mac = StringUtils.substring(drkey, 32); //mac地址
		//验证是否已激活
		boolean verifyActivate = verifyActivate(mac);
		if (!verifyActivate) {
			logger.error("点入广告点击记录,已被激活 drkey="+drkey);
			return response(false, "已被激活");
		}
		List<AdvertiseInfo> list = AdvertiseInfo.getListByMacSourceDrkey(mac, source, drkey);
		if (list==null||list.size()==0) {
			//保存记录
			saveAdvertiseInfoByDrkey(mac, drkey, source);
			return response(true, "通知成功");
		} else {
			return response(false, "重复记录");
		}
	}
	
	@Override
	public void notifyActivate(AdvertiseInfo advertiseInfo) {
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

	@Override
	public Map<String, Object> addScore(Map<String, String> param) {
		return null;
	}

}
