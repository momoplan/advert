package com.ruyicai.advert.center;

import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruyicai.advert.consts.AdvertiseSource;
import com.ruyicai.advert.consts.errorcode.RuanlieErrorCode;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.exception.RuanlieException;
import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.advert.util.PropertiesUtil;
import com.ruyicai.advert.util.StringUtil;

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
		String source = AdvertiseSource.ruanlie.value();
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
		
		String result = "";
		int requestCount = 1;
		while (StringUtil.isEmpty(result) && requestCount<4) {
			String url = propertiesUtil.getRuanlie_notifyUrl()+"?mac="+mac+"&idfa="+idfa+"&appId="+appId;
			result = HttpUtil.sendRequestByGet(url, true);
			//{"result":{"code":"000000","desc":"OK"}}
			logger.info("广告通知软猎返回:"+result+";adMac="+adMac+";requestCount="+requestCount);
			if (StringUtils.isNotBlank(result)) {
				JSONObject fromObject = JSONObject.fromObject(result);
				String code = fromObject.getJSONObject("result").getString("code");
				if (StringUtils.equals(code, "000000")) { //成功
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
	
	/*public static void main(String[] args) {
		String result = "{\"result\":{\"code\":\"000000\",\"desc\":\"OK\"}}";
		JSONObject fromObject = JSONObject.fromObject(result);
		String code = fromObject.getJSONObject("result").getString("code");
		if (StringUtils.equals(code, "000000")) {
			System.out.println("aa");
		}
	}*/

}
