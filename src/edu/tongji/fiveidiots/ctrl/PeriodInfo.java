package edu.tongji.fiveidiots.ctrl;
import java.util.HashMap;

/**
 * 一切只为处理那一个传说中的way变量——周期信息
 * @author Qrc @author Andriy
 * 
 * 读取任务周期信息：
 * 1. 先通过public int getPeriodType()得到任务的类型
 * 2. 根据不同类型分别调用：int getIntervalByDay() 或者  HashMap<Integer, Boolean> getCheckedListByWeek()
 * 
 * 存储任务周期信息：
 * 若为非周期任务，调用：void setPeriodNone()
 * 若为隔日循环任务，调用：void setPeriodByDay(int interval)
 * 若为每周执行任务，调用：void setPeriodByWeek(HashMap<Integer, Boolean> map)
 */
public class PeriodInfo {

	/**
	 * 周期类型，_NONE, _BY_DAY, _BY_WEEK
	 */
	private int type = PERIOD_NONE;

	/**
	 * 当PERIOD_BY_DAY的时候，每多少天
	 */
	private int interval = -1;

	/**
	 * 当PERIOD_BY_WEEK的时候，每周的哪几天
	 */
	private HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();

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
	 * 默认生成的是PERIOD_NONE
	 */
	public PeriodInfo() {}

	public PeriodInfo (int way) {
		type = way >> 7;
		map = new HashMap<Integer, Boolean>(7);
		if (type == PERIOD_BY_DAY){
			interval = way - (type << 7);
		}
		if (type == PERIOD_BY_WEEK){
			for ( int i = 1; i <= 7; ++ i){
				if (way % 2 == 1) map.put(i, true);
				else map.put(i, false);
				way = way >> 1;
			}
		}
	}

	/**
	 * 有没有重复、有则是通过每隔几天还是每周周几
	 * @return PERIOD_NONE, PERIOD_BY_DAY, PERIOD_BY_WEEK
	 */
	public int getPeriodType() {
		return type;
	}

	/**
	 * 在PERIOD_BY_DAY模式下，返回其interval，否则扔个异常吧
	 * @return
	 */
	public int getIntervalByDay() {
		if (this.getPeriodType() != PERIOD_BY_DAY) {
			throw new IllegalStateException("应该先判断周期类型，如果不是BY_DAY就别调这里了");
		}

		return interval;
	}

	/**
	 * 在PERIOD_BY_WEEK模式下，返回一个Map，1->7分别对应周一到周日，true就是要重复
	 * @return
	 */
	public HashMap<Integer, Boolean> getCheckedMapByWeek() {
		if (this.getPeriodType() != PERIOD_BY_WEEK) {
			throw new IllegalStateException("应该先判断周期类型，如果不是BY_WEEK就别调这里了");
		}

		return map;
	}

	/**
	 * 设置为BY_DAY模式，并设置其interval
	 * @param interval
	 */
	public void setPeriodByDay(int interval) {
		this.interval = interval;
		type = PERIOD_BY_DAY;
	}

	/**
	 * 设置为BY_WEEK模式，根据传入的<周几, 要不要>的map，其中Integer从
	 * @param map 其key，Integer从1->7
	 */
	public void setPeriodByWeek(HashMap<Integer, Boolean> map) {
		this.map = map;
		type = PERIOD_BY_WEEK;
	}

	/**
	 * 设置为没有周期、没有重复
	 */
	public void setPeriodNone() {
		type = PERIOD_NONE;
	}
	
	public int TranslateKey(){
		int way = type << 7;
		if (type == PERIOD_BY_DAY) way += interval;
		if (type == PERIOD_BY_WEEK){
			for ( int i = 1; i <= 7; ++ i){
				if (map.get(i)) way += 1 << (i-1);
			}
		}
		return way;
	}
}
