package edu.tongji.fiveidiots.ctrl;

import java.util.HashMap;
import java.util.Map;

/**
 * 一切只为处理那一个传说中的way变量——周期信息
 * @author qrc @author Andriy
 */
public class PeriodInfo {

	/**
	 * 所有信息被压缩到每一位，存储在这个int里
	 */
	private int key;
	
	/**
	 * 没有重复
	 */
	public static final int PERIOD_NONE = 0;
	/**
	 * 每隔几天的重复
	 */
	public static final int PERIOD_BY_DAY = 1;
	/**
	 * 每周周几的重复
	 */
	public static final int PERIOD_BY_WEEK = 2;
	
	/**
	 * 从外界设置这个key，比如从数据库读出的时候
	 */
	public void setKey(int aKey) {
		this.key = aKey;
	}
	
	/**
	 * 将所有信息整合成一个int，在存入数据库的时候可以很方便、很省空间
	 */
	public int getKey() {
		return this.key;
	}
	
	/**
	 * 有没有重复、有则是通过每隔几天还是每周周几
	 * @return PERIOD_NONE, PERIOD_BY_DAY, PERIOD_BY_WEEK
	 */
	public int getPeriodType() {
		//TODO 填完这里
		return PERIOD_NONE;
	}
	
	/**
	 * 在PERIOD_BY_DAY模式下，返回其interval，否则扔个异常吧
	 * @return
	 */
	public int getIntervalByDay() {
		if (this.getPeriodType() != PERIOD_BY_DAY) {
			throw new IllegalStateException("应该先判断周期类型，如果不是BY_DAY就别调这里了");
		}
		
		//TODO 填完这里
		return 0;
	}
	
	/**
	 * 在PERIOD_BY_WEEK模式下，返回一个Map，1->7分别对应周一到周日，true就是要重复
	 * @return
	 */
	public HashMap<Integer, Boolean> getCheckedListByWeek() {
		if (this.getPeriodType() != PERIOD_BY_WEEK) {
			throw new IllegalStateException("应该先判断周期类型，如果不是BY_WEEK就别调这里了");
		}
		
		HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>(7);

		//TODO 改完这里
		map.put(1, true);
		map.put(2, true);
		map.put(3, true);
		map.put(4, true);
		map.put(5, true);
		map.put(6, true);
		map.put(7, true);
		
		return map;
	}
	
	/**
	 * 设置为BY_DAY模式，并设置其interval
	 * @param interval
	 */
	public void setPeriodByDay(int interval) {
		//TODO 填满这里
	}
	
	/**
	 * 设置为BY_WEEK模式，根据传入的<周几, 要不要>的map，其中Integer从
	 * @param map 其key，Integer从1->7
	 */
	public void setPeriodByWeek(Map<Integer, Boolean> map) {
		//TODO 填满这里
	}
	
	/**
	 * 设置为没有周期、没有重复
	 */
	public void setPeriodNone() {
		//TODO  填满这里
	}
}
