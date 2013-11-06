package com.ruyicai.advert.util;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.center.ScoreWall;
import com.ruyicai.advert.domain.AdvertiseInfo;
import com.ruyicai.advert.service.AdvertManager;

/**
 * 广告公共类
 * @author Administrator
 *
 */
@Service
public class AdvertiseUtil {

	private Logger logger = Logger.getLogger(AdvertiseUtil.class);
	
	@Autowired
	private AdvertManager advertManager;
	
	public AdvertiseInfo getValidAdvertiseInfo(String mac, String source) {
		List<AdvertiseInfo> advertiseInfos = AdvertiseInfo.getListByMacSource(mac, source);
		if (advertiseInfos!=null && advertiseInfos.size()>0) {
			for (AdvertiseInfo advertiseInfo : advertiseInfos) {
				String state = advertiseInfo.getState();
				if (StringUtils.equals(state, "0")) { //已经激活过
					return advertiseInfo;
				}
			}
			return advertiseInfos.get(0);
		}
		return null;
	}
	
	public AdvertiseInfo getValidAdvertiseInfo(String mac) {
		List<AdvertiseInfo> advertiseInfos = AdvertiseInfo.getListByMac(mac);
		if (advertiseInfos!=null && advertiseInfos.size()>0) {
			for (AdvertiseInfo advertiseInfo : advertiseInfos) {
				String state = advertiseInfo.getState();
				if (StringUtils.equals(state, "0")) { //已经激活过
					return advertiseInfo;
				}
			}
			return advertiseInfos.get(0);
		}
		return null;
	}
	
	/**
	 * 广告通知第三方
	 * @param coopId
	 */
	public void notifyThirdParty(AdvertiseInfo advertiseInfo) {
		try {
			if (advertiseInfo!=null&&StringUtils.equals(advertiseInfo.getState(), "1")) { //未通知过第三方
				String source = advertiseInfo.getSource(); //渠道
				ScoreWall scoreWall = advertManager.getScoreWall(source);
				if (scoreWall==null) {
					logger.error("广告通知第三方时获取积分墙为空,source="+source);
					return ;
				}
				scoreWall.notifyActivate(advertiseInfo);
			}
		} catch (Exception e) {
			logger.error("广告通知第三方发生异常", e);
		}
	}
	
}
