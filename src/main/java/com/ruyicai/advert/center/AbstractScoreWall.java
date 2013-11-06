package com.ruyicai.advert.center;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.ruyicai.advert.domain.AdvertiseInfo;

public abstract class AbstractScoreWall implements ScoreWall {
	
	private Logger logger = Logger.getLogger(AbstractScoreWall.class);
	
	protected String ip;
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Map<String, Object> response(Object success, String message) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", success);
		resultMap.put("message", message);
		return resultMap;
	}
	
	/**
	 * 验证ip
	 * @param ip
	 * @param rightIp
	 * @return
	 */
	public static boolean verfyIp(String ip, String rightIp) {
		if (StringUtils.isBlank(ip)) {
			return false;
		}
		List<String> rightIpList = new ArrayList<String>();
		String[] split = StringUtils.split(rightIp, ",");
		for (String string : split) {
			if (StringUtils.isBlank(string)) {
				continue;
			}
			if (StringUtils.indexOf(string, "-")>-1) {
				String[] split2 = StringUtils.split(string, "-");
				String prefix = StringUtils.substringBeforeLast(split2[0], ".");
				String startIndex = StringUtils.substringAfterLast(split2[0], ".");
				String endIndex = StringUtils.substringAfterLast(split2[1], ".");
				for (int i = Integer.valueOf(startIndex); i <= Integer.valueOf(endIndex); i++) {
					rightIpList.add(prefix+"."+i);
				}
			} else {
				rightIpList.add(string);
			}
		}
		String[] ips = StringUtils.split(ip, ",");
		for (String p : ips) {
			if (StringUtils.isNotBlank(p)&&rightIpList.contains(p.trim())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 验证是否已经激活
	 * @param mac
	 * @return true:未激活;false:已激活
	 */
	public boolean verifyActivate(String mac) {
		try {
			List<AdvertiseInfo> list = AdvertiseInfo.getListByMac(mac);
			if (list!=null && list.size()>0) {
				for (AdvertiseInfo advertiseInfo : list) {
					if (StringUtils.equals(advertiseInfo.getState(), "0")) { //已激活
						return false;
					}
				}
			}
		} catch (Exception e) {
			logger.info("广告通知-验证是否已经激活,mac="+mac+",发生异常:", e);
		}
		return true;
	}
	
	/**
	 * 根据appId保存广告记录
	 * @param mac
	 * @param appId
	 * @param source
	 */
	public void saveAdvertiseInfoByAppid(String mac, String appId, String source) {
		AdvertiseInfo advertiseInfo = new AdvertiseInfo();
		advertiseInfo.setMac(mac);
		advertiseInfo.setAppid(appId);
		advertiseInfo.setSource(source);
		advertiseInfo.setCreatetime(new Date());
		advertiseInfo.setUpdatetime(new Date());
		advertiseInfo.setState("1");
		advertiseInfo.persist();
	}
	
	/**
	 * 根据drkey保存广告记录
	 * @param mac
	 * @param drkey
	 * @param source
	 */
	public void saveAdvertiseInfoByDrkey(String mac, String drkey, String source) {
		AdvertiseInfo advertiseInfo = new AdvertiseInfo();
		advertiseInfo.setMac(mac);
		advertiseInfo.setDrkey(drkey);
		advertiseInfo.setSource(source);
		advertiseInfo.setCreatetime(new Date());
		advertiseInfo.setUpdatetime(new Date());
		advertiseInfo.setState("1");
		advertiseInfo.persist();
	}
	
	/**
	 * 更新AdvertiseInfo的状态
	 * @param advertiseInfo
	 */
	public void updateAdvertiseInfoState(AdvertiseInfo advertiseInfo) {
		advertiseInfo.setState("0");
		advertiseInfo.setUpdatetime(new Date());
		advertiseInfo.merge();
	}
	
}
