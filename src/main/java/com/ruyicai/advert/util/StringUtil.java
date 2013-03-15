package com.ruyicai.advert.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 字符串工具类
 * @author Administrator
 *
 */
public class StringUtil {
	
	private static Logger logger = Logger.getLogger(StringUtil.class);

	/**
	 * 验证参数是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String string) {
		if (StringUtils.isEmpty(string)) {
			return true;
		}
		if (string.trim().equals("")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 去掉字符串结尾的字符
	 * @param string
	 * @param endCharacter
	 * @return
	 */
	public static String removeEndCharacter(String string, String endCharacter) {
		if (!isEmpty(string)&&string.endsWith(endCharacter)) {
			string = string.substring(0, string.length()-endCharacter.length());
		}
		return string;
	}
	
	/**
	 * 将字符串数组用符合连接
	 * @param list
	 * @param character
	 * @return
	 */
	public static String joinStringArrayWithCharacter(List<String> list, String character) {
		StringBuilder builder = new StringBuilder();
		if (list!=null&&list.size()>0) {
			for (String string : list) {
				builder.append(string).append(character);
			}
		}
		return removeEndCharacter(builder.toString(), character);
	}
	
	/**
	 * 将注码转成数组
	 * @param code 注码
	 * @param num (1位还是2位)
	 * @return
	 */
	public static List<String> getStringArrayFromString(String code, Integer num) {
		List<String> list = new ArrayList<String>();
		try {
			int l = code.length();
			int h = l / num;
			int n = 0;
			for (int i = 0; i < h; i++) {
				String ss = code.substring(n, n + num);
				n = n + num;
				list.add(ss);
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
		return list;
	}
	
}
