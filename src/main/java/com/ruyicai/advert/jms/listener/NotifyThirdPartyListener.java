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
			if (StringUtil.isEmpty(mac)) {
				return ;
			}
			//查用户表,防止通过其他渠道激活的用户通知第三方
			List<UserInf> list = getUserInfByMac(mac);
			if (list!=null&&list.size()>0) { //有激活记录
				UserInf userInf = list.get(0);
				String channel = userInf.getChannel(); //渠道号
				String source = advertiseUtil.getSourceByChannel(channel);
				if (StringUtil.isEmpty(source)) { //通过其他渠道激活
					return ;
				}
				//通知第三方积分墙
				AdvertiseInfo advertiseInfo = advertiseUtil.getAdvertiseInfoBySourceAndMac(source, mac);
				advertiseUtil.notifyThirdParty(advertiseInfo);
			} else { //由于客户端的原因,没有激活记录就注册的,也要通知第三方
				AdvertiseInfo advertiseInfo = advertiseUtil.getAdvertiseInfo(mac);
				advertiseUtil.notifyThirdParty(advertiseInfo); //通知第三方
			}
		} catch (Exception e) {
			logger.error("通知第三方时发生异常imei="+imei+",mac="+mac, e);
		}
	}
	
	/**
	 * 根据mac查询UserInf
	 * @param mac
	 * @return
	 */
	private List<UserInf> getUserInfByMac(String mac) {
		StringBuilder builder = new StringBuilder(" where");
		List<Object> params = new ArrayList<Object>();
		
		builder.append(" o.mac=? and");
		params.add(mac);
		
		builder.append(" o.platfrom=? ");
		params.add(Platform.iPhone.value());
		
		List<UserInf> list = UserInf.getList(builder.toString(), "order by o.createtime desc", params);
		return list;
	}
	
}
