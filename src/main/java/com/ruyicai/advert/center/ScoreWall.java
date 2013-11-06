package com.ruyicai.advert.center;

import java.util.Map;
import com.ruyicai.advert.domain.AdvertiseInfo;

public interface ScoreWall {

	/**
	 * 接收广告点击
	 * @param param
	 * @return
	 */
	public Map<String, Object> receiveAdvertise(Map<String, String> param);
	
	/**
	 * 通知积分墙激活
	 * @param advertiseInfo
	 */
	public void notifyActivate(AdvertiseInfo advertiseInfo);
	
	/**
	 * 积分墙加积分
	 * @param param
	 * @return
	 */
	public Map<String, Object> addScore(Map<String, String> param);
	
}
