package edu.tongji.fiveidiots.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 处理时间相关的操作，如生成描述字符串等
 */
public class TimeUtil {

	/**
	 * 比如说在番茄钟内，根据 @param seconds 显示剩余多少时间
	 * @author Andriy
	 */
	public static String parseRemainingTime(long seconds) {
		if (seconds >= 60) {
			long minute = seconds / 60;
			long hour = minute / 60;
			minute %= 60;
			if (hour == 0) {
				return minute + "分钟";
			}
			else {
				return hour + "小时" + minute + "分钟";
			}
		}
		
		return seconds + "秒";
	}
	
	/**
	 * @return 得到一个date的yyyy-MM-dd HH:mm的格式字符串
	 * @param d : date
	 * @author Andriy
	 */
	public static String parseDateTime(Date d) {
		if (d == null) {
			throw new IllegalArgumentException("别传个null的参数date进来啊");
		}

		String format = "yyyy-MM-dd HH:mm";
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(d);
	}
	
	/**
	 * @return 得到一个date的yyyy-MM-dd的格式字符串
	 * @param d : date
	 * @author Andriy
	 */
	public static String parseDate(Date d) {
		if (d == null) {
			throw new IllegalArgumentException("别传个null的参数date进来啊");
		}

		String format = "yyyy-MM-dd";
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(d);
	}
	
	/**
	 * 决定一个date是不是代表全天，即其时分为00:00
	 * @param date
	 * @return
	 */
	public static boolean isFullDay(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("别传个null的参数date进来啊");
		}

		return date.getHours() == 0 && date.getMinutes() == 0; 
	}
}
