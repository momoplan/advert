package com.ruyicai.advert.jms.listener;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Header;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.consts.Platform;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.domain.UserInf;
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
			if (StringUtil.isEmpty(platform)||!platform.equals(Platform.iPhone.value())) {
		        logger.error("通知第三方时平台不是苹果,Imei=" + imei + ";platform=" + platform + ";mac=" + mac);
		        return ;
		    }
			StringBuilder builder = new StringBuilder(" where");
			List<Object> params = new ArrayList<Object>();
			
			builder.append(" o.mac=? and");
			params.add(mac);
			
			builder.append(" o.platfrom=?");
			params.add(Platform.iPhone.value());
			
			List<UserInf> list = UserInf.getList(builder.toString(), " order by o.createtime desc", params);
			if (list==null||list.size()<=0) {
				logger.error("通知第三方时用户表记录为空,Imei="+imei+",mac="+mac);
				return ;
			}
			UserInf userInf = list.get(0);
			//通知第三方积分墙
			String channel = userInf.getChannel(); //渠道号
			String source = advertiseUtil.getSourceByChannel(channel);
			AdvertiseInfo advertiseInfo = advertiseUtil.getAdvertiseInfoBySourceAndMac(source, mac);
			advertiseUtil.notifyThirdParty(advertiseInfo); //通知第三方
		} catch (Exception e) {
			logger.error("通知第三方时发生异常imei="+imei+",mac="+mac, e);
		}
		//logger.info("通知第三方的jms end "+"imei="+imei+";platform="+platform+";mac="+mac);
	}
	
}
