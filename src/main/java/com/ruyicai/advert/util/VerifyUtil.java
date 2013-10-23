package com.ruyicai.advert.util;

import java.util.ArrayList;
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
	
}
