package com.ruyicai.advert.center;

import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruyicai.advert.consts.DomobErrorCode;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.exception.DomobException;
import com.ruyicai.advert.util.HttpUtil;
import com.ruyicai.advert.util.PropertiesUtil;
import com.ruyicai.advert.util.StringUtil;
import com.ruyicai.advert.util.Tools;

@Component("domob")
public class Domob extends AbstractScoreWall {

	private Logger logger = Logger.getLogger(Domob.class);
	
	@Autowired
	private PropertiesUtil propertiesUtil;
	
	@Override
	public void init() {
		this.setIp(propertiesUtil.getDomobIp());
	}
	
	@Override
	public Map<String, Object> receiveAdvertise(Map<String, String> param) {
		//String ip = param.get("ip");
		String appId = param.get("appId");
		String mac = param.get("mac");
		String idfa = param.get("idfa");
		String source = param.get("source");
		//验证ip
		boolean verfyIp = verfyIp(ip, getIp());
		if (!verfyIp) {
			logger.error("多盟广告点击记录,ip不合法 mac="+mac+";idfa="+idfa+";appId="+appId+";ip="+ip);
			throw new DomobException(DomobErrorCode.exception);
		}
		//取设备唯一标识
		mac = (StringUtils.isNotBlank(mac)&&!StringUtils.equals(mac, "02:00:00:00:00:00")) ? mac : idfa;
		//验证是否已激活
		boolean verifyActivate = verifyActivate(mac);
		if (!verifyActivate) {
			logger.error("多盟广告点击记录,已被激活 mac="+mac);
			throw new DomobException(DomobErrorCode.hasActive);
		}
		List<AdvertiseInfo> list = AdvertiseInfo.getListByMacSourceAppid(mac, source, appId);
		if (list==null||list.size()==0) {
			//保存记录
			saveAdvertiseInfoByAppid(mac, appId, source);
		} else {
			throw new DomobException(DomobErrorCode.repeatRecord);
		}
		return null;
	}
	
	@Override
	public void notifyActivate(AdvertiseInfo advertiseInfo) {
		String appId = advertiseInfo.getAppid();
		String adMac = advertiseInfo.getMac();
		
		String mac = "";
		String idfa = "";
		if (StringUtils.indexOf(adMac, ":")>-1) {
			mac = adMac;
		} else {
			idfa = adMac;
		}
		long acttime = System.currentTimeMillis();
		String sign = getSign(appId, mac, idfa);
		
		String result = "";
		int requestCount = 1;
		while (StringUtil.isEmpty(result) && requestCount<4) {
			String url = propertiesUtil.getDomob_notifyUrl()+"?appId="+appId+"&udid="+mac+"&ifa="+idfa+
					"&acttime="+acttime+"&returnFormat=1"+"&sign="+sign;
			result = HttpUtil.sendRequestByGet(url, true);
			logger.info("广告通知多盟返回:"+result+";adMac="+adMac+";requestCount="+requestCount);
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
	
	private String getSign(String appId, String mac, String idfa) {
		StringBuilder builder = new StringBuilder();
		builder.append(appId).append(","); //appid
		builder.append(mac).append(","); //udid
		builder.append("").append(","); //ma
		builder.append(idfa).append(","); //ifa
		builder.append("").append(","); //oid
		builder.append("3f70a6bced98f727d7f492844bd15132"); //key
		return Tools.md5(builder.toString());
	}
	
	/*public static void main(String[] args) {
		//String string = Tools.md5("4498731,7C:AB:A3:D6:E7:81,,511F7987-6E2F-423A-BFED-E4C52CB5A6DC,,123456");
		//System.out.println(string);
		
		String sign = new Domob().getSign("492164095", "E8:8D:28:D6:4A:B6", "");
		System.out.println(sign);
	}*/

}
