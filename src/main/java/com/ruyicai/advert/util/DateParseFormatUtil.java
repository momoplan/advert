package com.ruyicai.advert.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

/**
 * 日期公共类
 * @author Administrator
 *
 */
public class DateParseFormatUtil {
	
	private static Calendar calendar = Calendar.getInstance();
	
	/**
	 * 获得今日日期
	 * @return
	 */
	/*public static String getTodayDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}*/
	
	/**
	 * 获得前pre个月的日期
	 * @param pre
	 * @return
	 */
	public static String getPreMonthDate(int pre, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -pre); //将当前日期加pre个月
		return sdf.format(calendar.getTime());
	}
	
	/**
	 * 获得前pre天的日期
	 * @param pre
	 * @return
	 */
	public static String getPreDayDate(int pre, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, -pre);
		return sdf.format(calendar.getTime());
	}
	
	/**
	 * 获取以后几天日期
	 * @param after
	 * @param format
	 * @return
	 */
	public static String getAfterDayDate(int after, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, after);
		return sdf.format(calendar.getTime());
	}
	
	/**
	 * dateString转Date
	 * @param dateString
	 * @param format
	 * @return
	 */
	public static Date parseDateString(String dateString, String format) {
		try {
			if (StringUtils.isBlank(dateString)) {
				return null;
			}
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据指定格式格式化时间
	 * @param time
	 * @param format 时间格式
	 * @return
	 */
	public static String formatDate(String time, String format) {
		if (StringUtils.isBlank(time)||StringUtils.equals(time, "null")) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(Long.parseLong(time));
	}
	
	/*public static void main(String[] args) {
		String time = formatDate("1401408000000", "yyyy-MM-dd HH:mm:ss");
		String endtime = formatDate("1401378900000", "yyyy-MM-dd HH:mm:ss");
		System.out.println(time);
		System.out.println(endtime);
	}*/
	
	/**
	 * 读取两个日期之间的天数 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static String getBetweenDays(String startTime, String endTime) {   
		try {
			if (StringUtil.isBlank(startTime)||StringUtil.isBlank(endTime)) {
				return "";
			}
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");   
			Date d1 = format.parse(startTime);   
			Date d2 = format.parse(endTime);   
			Calendar c1 = Calendar.getInstance();   
			Calendar c2 = Calendar.getInstance();   
			c1.setTime(d1);   
			c2.setTime(d2);   
			// 保证第二个时间一定大于第一个时间   
			if(c1.after(c2)) {   
			    c1.setTime(d2);   
			    c2.setTime(d1);   
			}
			int betweenYears = c2.get(Calendar.YEAR)-c1.get(Calendar.YEAR);
			int betweenDays = c2.get(Calendar.DAY_OF_YEAR)-c1.get(Calendar.DAY_OF_YEAR);   
			for(int i=0; i<betweenYears; i++) {   
			    betweenDays += countDays(c1.get(Calendar.YEAR));  
			    c1.set(Calendar.YEAR,(c1.get(Calendar.YEAR)+1));   
			}   
			return String.valueOf(betweenDays);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
    }   
	
	public static int countDays(int year) {  
        int n=0;  
        for (int i = 1; i <= 12; i++) {    
            n += countDays(i,year);    
        }    
        return n;  
    }
	
	public static int countDays(int month, int year) {    
        int count = -1;    
        switch(month) {    
          case 1:    
          case 3:       
          case 5:    
          case 7:    
          case 8:    
          case 10:    
          case 12:    
            count = 31;    
            break;    
          case 4:    
          case 6:    
          case 9:    
          case 11:    
              count = 30;    
              break;    
          case 2:    
              if(year % 4 == 0)    
                  count = 29;    
              else    
                  count = 28;    
              if((year % 100 ==0) & (year % 400 != 0))    
                      count = 28;    
        }    
        return count;    
    }
	
}
