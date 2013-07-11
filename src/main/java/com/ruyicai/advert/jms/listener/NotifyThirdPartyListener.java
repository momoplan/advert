package com.ruyicai.advert.jms.listener;

import org.apache.camel.Header;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.util.AdvertiseUtil;
import com.ruyicai.advert.util.StringUtil;

/**
 *  通知第三方的jms
 * @author Administrator
 *
 */
@Service
public class NotifyThirdPartyListener {

	private Logger logger = Logger.getLogger(NotifyThirdPartyListener.class);
	
	@Autowired
	private AdvertiseUtil advertiseUtil;
	
	public void notify(@Header("imei") String imei, @Header("platform") String platform, @Header("mac") String mac) {
		logger.info("通知第三方的jms start "+"imei="+imei+";platform="+platform+";mac="+mac);
		try {
			if (StringUtil.isEmpty(mac)) {
				return ;
			}
			AdvertiseInfo advertiseInfo = advertiseUtil.getAdvertiseInfo(mac);
			advertiseUtil.notifyThirdParty(advertiseInfo); //通知第三方
		} catch (Exception e) {
			logger.error("通知第三方时发生异常imei="+imei+",mac="+mac, e);
		}
		//logger.info("通知第三方的jms end "+"imei="+imei+";platform="+platform+";mac="+mac);
	}
	
}
