package com.ruyicai.advert.center;

import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruyicai.advert.consts.errorcode.WangyuErrorCode;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.exception.WangyuException;
import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.advert.util.PropertiesUtil;
import com.ruyicai.advert.util.StringUtil;

@Component("wangyu")
public class Wangyu extends AbstractScoreWall {

	private Logger logger = Logger.getLogger(Wangyu.class);
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	@Override
	public void init() {
		this.setIp(propertiesUtil.getWangyuIp());
	}
	
	@Override
	public Map<String, Object> receiveAdvertise(Map<String, String> param) {
		String ip = param.get("ip");
		String cid = param.get("cid");
		String deviceid = param.get("deviceid");
		//验证ip
		boolean verfyIp = verfyIp(ip, getIp());
		if (!verfyIp) {
			logger.error("网域广告点击记录,ip不合法 mac="+deviceid+";cid="+cid+";ip="+ip);
			throw new WangyuException(WangyuErrorCode.exception);
		}
		//验证是否已激活
		boolean verifyActivate = verifyActivate(deviceid);
		if (!verifyActivate) {
			logger.error("网域广告点击记录,已被激活 mac="+deviceid);
			throw new WangyuException(WangyuErrorCode.hasActive);
		}
		String appId = "649";
		List<AdvertiseInfo> list = AdvertiseInfo.getListByMacSourceAppid(deviceid, cid, appId);
		if (list==null||list.size()==0) {
			//保存记录
			saveAdvertiseInfoByAppid(deviceid, appId, cid);
		} else {
			throw new WangyuException(WangyuErrorCode.repeatRecord);
		}
		return null;
	}

	@Override
	public void notifyActivate(AdvertiseInfo advertiseInfo) {
		String appId = advertiseInfo.getAppid();
		String adMac = advertiseInfo.getMac();
		
		String result = "";
		int requestCount = 1;
		while (StringUtil.isEmpty(result) && requestCount<4) {
			String url = propertiesUtil.getWangyu_notifyUrl()+"?ver=2"+"&appid="+appId+
					"&deviceid="+adMac+"&outformat=2&check=d8d85766406036e975fa1b1383ede2eb";
			result = HttpUtil.sendRequestByGet(url, true);
			logger.info("广告通知网域返回:"+result+";adMac="+adMac+";requestCount="+requestCount);
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
