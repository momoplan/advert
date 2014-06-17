package com.ruyicai.advert.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;

/**
 * 工具类
 * 
 * @author Administrator
 * 
 */
public class Tools {

	private static Logger logger = Logger.getLogger(Tools.class);

	/**
	 * MD5加密（外部合作使用）
	 * 
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		String result = "";
		try {
			byte[] btInput = str.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < md.length; i++) {
				int val = ((int) md[i]) & 0xff;
				if (val < 16) {
					sb.append("0");
				}
				sb.append(Integer.toHexString(val));
			}
			result = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			logger.error("error", e);
		}
		return result;
	}
	
	/**
	 * hmac-sha1 加密模式
	 * @param data
	 * @param key
	 * @return
	 */
	public static String hmac(String data, String key) {
		byte[] byteHMAC = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
			mac.init(spec);
			byteHMAC = mac.doFinal(data.getBytes());
		} catch (Exception e) {
			logger.error("error", e);
		}
		String oauth = new BASE64Encoder().encode(byteHMAC);
		return oauth;
	}

	/**
	 * 按key排序Map
	 * 
	 * @param map
	 * @param reverse
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map sortMapByKey(Map map, final boolean reverse) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				if (reverse) {
					return -((Comparable) ((Map.Entry) (o1)).getKey())
							.compareTo(((Map.Entry) (o2)).getKey());
				}
				return ((Comparable) ((Map.Entry) (o1)).getKey())
						.compareTo(((Map.Entry) (o2)).getKey());
			}
		});

		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}