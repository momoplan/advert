package com.ruyicai.advert.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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


}