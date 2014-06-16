package com.ruyicai.advert.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class QqService {

	private Logger logger = Logger.getLogger(QqService.class);
	
	/**
	 * 应用宝任务集市
	 * @param cmd
	 * @param openid
	 * @param appid
	 * @param ts
	 * @param version
	 * @param contractid
	 * @param step
	 * @param payitem
	 * @param billno
	 * @param pkey
	 * @param sig
	 */
	public void taskMarket(String cmd, String openid, String appid, String ts, 
			String version, String contractid, String step, String payitem, 
			String billno, String pkey, String sig) {
		logger.info("应用宝任务集市,cmd:"+cmd+",openid:"+openid+",appid:"+appid+",ts:"+ts+
				",version:"+version+",contractid:"+contractid+",step:"+step+",payitem:"+payitem+
				",billno:"+billno+",pkey:"+pkey+",sig:"+sig);
		
	}
	
}
