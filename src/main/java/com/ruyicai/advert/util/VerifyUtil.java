package com.ruyicai.advert.util;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class VerifyUtil {

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
		String[] ips = StringUtils.split(ip, ",");
		String[] limeiIps = StringUtils.split(rightIp, ",");
		List<String> limeiIpList = Arrays.asList(limeiIps);
		for (String p : ips) {
			if (StringUtils.isNotBlank(p)&&limeiIpList.contains(p.trim())) {
				return true;
			}
		}
		return false;
	}
	
}
