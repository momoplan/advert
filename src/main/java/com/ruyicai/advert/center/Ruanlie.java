package com.ruyicai.advert.center;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruyicai.advert.consts.AdvertiseSource;
import com.ruyicai.advert.consts.RuanlieErrorCode;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.exception.RuanlieException;
import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.advert.util.PropertiesUtil;

@Component("ruanlie")
public class Ruanlie extends AbstractScoreWall {

	private Logger logger = Logger.getLogger(Ruanlie.class);
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	@Override
	public void init() {
		this.setIp(propertiesUtil.getRuanlieIp());
	}
	
	@Override
	public Map<String, Object> receiveAdvertise(Map<String, String> param) {
		String source = AdvertiseSource.ruanlie.value();
		String ip = param.get("ip");
		String mac = param.get("mac");
		String idfa = param.get("idfa");
		String appId = param.get("appId");
		//验证ip
		boolean verfyIp = verfyIp(ip, getIp());
		if (!verfyIp) {
			logger.error("软猎广告点击记录,ip不合法 mac="+mac+";idfa="+idfa+";appId="+appId+";ip="+ip);
			throw new RuanlieException(RuanlieErrorCode.exception);
		}
		//取设备唯一标识
		mac = StringUtils.isNotBlank(mac) ? mac : idfa;
		//验证是否已激活
		boolean verifyActivate = verifyActivate(mac);
		if (!verifyActivate) {
			logger.error("软猎广告点击记录,已被激活 mac="+mac);
			throw new RuanlieException(RuanlieErrorCode.hasActive);
		}
		List<AdvertiseInfo> list = AdvertiseInfo.getListByMacSourceAppid(mac, source, appId);
		if (list==null||list.size()==0) {
			//保存记录
			saveAdvertiseInfoByAppid(mac, appId, source);
		} else {
			throw new RuanlieException(RuanlieErrorCode.repeatRecord);
		}
		return null;
	}

	@Override
	public void notifyActivate(AdvertiseInfo advertiseInfo) {
		String adMac = advertiseInfo.getMac(); //广告传过来的mac地址
		String appId = advertiseInfo.getAppid(); //app标识
		
		String mac = "";
		String idfa = "";
		if (StringUtils.indexOf(adMac, ":")>-1) {
			mac = adMac;
		} else {
			idfa = adMac;
		}
		String url = propertiesUtil.getRuanlie_notifyUrl()+"?mac="+mac+"&idfa="+idfa+"&appId="+appId;
		String result = HttpUtil.sendRequestByGet(url, true);
		logger.info("广告通知软猎返回:"+result+";adMac="+adMac);
	}

	@Override
	public Map<String, Object> addScore(Map<String, String> param) {
		return null;
	}

}
