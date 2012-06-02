package edu.tongji.fiveidiots.util;

import edu.tongji.fiveidiots.ui.PomotimerService;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用来处理用户的设置（配置）
 */
public class Settings {

	/**
	 * 这里放所有默认的设定吧
	 * @author Andriy
	 */
	public static class DefaultSettings {
		/** 默认一个番茄钟时间周期是25min */
		public static final int POMOTIMER_DURATION = 25;

		/** 默认番茄计时器的状态 */
		public static final int TIMER_STATE = PomotimerService.STATE_IDLE;
		/** 默认番茄计时器当前周期的总时间 */
		public static final long TIMER_TOTAL_TIME = 0;
		/** 默认番茄计时器当前周期的剩余时间 */
		public static final long TIMER_REMAIN_TIME = 0;
		/** 默认番茄计时器当前对应的TASK_ID */
		public static final long TIMER_CUR_TASK_ID = -1;
	}
	
	private static final String IDOIT_SETTINGS_STR = "idoit-tongji_settings";
	private final SharedPreferences preferences;
	
	public Settings(Context context) {
		this.preferences = context.getSharedPreferences(IDOIT_SETTINGS_STR, Context.MODE_PRIVATE);
	}
	
	/**
	 * 重置所有的参数为默认值
	 */
	public void reset() {
		this.setPomotimerDuration(DefaultSettings.POMOTIMER_DURATION);
		new TimerTempSettings().reset();
	}
	
	//==========
	private static final String POMOTIMER_DURATION_STR = "pomo_timer_duration";
	/** 
	 * @return 一个番茄钟周期的时间，单位分钟
	 */
	public int getPomotimerDuration() {
		return preferences.getInt(POMOTIMER_DURATION_STR, DefaultSettings.POMOTIMER_DURATION);
	}
	/**
	 * 设置一个番茄钟周期的时间
	 * @param minute 单位：分钟
	 */
	public void setPomotimerDuration(int minute) {
		preferences.edit().putInt(POMOTIMER_DURATION_STR, minute).commit();
	}

	/**
	 * 一个类with有参构造函数，其内部类with无参构造函数，怎么搞！
	 * @return 一个TimerTempSettings实例
	 */
	public TimerTempSettings getTimerSettings() {
		return new TimerTempSettings();
	}

	/**
	 * 专门存储和番茄计时器相关的参数
	 * @author Andriy
	 */
	public class TimerTempSettings {
		private TimerTempSettings() {}
		
		/**
		 * 重置那些和番茄计时器相关的参数
		 */
		public void reset() {
			this.setTimerState(DefaultSettings.TIMER_STATE);
			this.setTotalTime(DefaultSettings.TIMER_TOTAL_TIME);
			this.setRemainTime(DefaultSettings.TIMER_REMAIN_TIME);
			this.setCurrentTaskID(DefaultSettings.TIMER_CUR_TASK_ID);
		}
		
		//==========
		private static final String TIMER_STATE_STR = "temp_timer_state";
		/**
		 * @return 上一次存储的计时器的状态
		 */
		public int getTimerState() {
			return preferences.getInt(TIMER_STATE_STR, PomotimerService.STATE_IDLE);
		}
		/**
		 * 设置这一次计时器的状态
		 * @param state
		 */
		public void setTimerState(int state) {
			preferences.edit().putInt(TIMER_STATE_STR, state).commit();
		}
		
		//==========
		private static final String TOTAL_TIME_STR = "temp_total_time";
		/**
		 * @return 上一次存储的一次番茄周期总时间
		 */
		public long getTotalTime() {
			return preferences.getLong(TOTAL_TIME_STR, 0);
		}
		/**
		 * 设置这一次番茄周期的总时间
		 * @param seconds
		 */
		public void setTotalTime(long seconds) {
			preferences.edit().putLong(TOTAL_TIME_STR, seconds).commit();
		}
		
		//==========
		private static final String REMAIN_TIME_STR = "temp_remain_time";
		/**
		 * @return 上一次存储的此次番茄周期的剩余时间
		 */
		public long getRemainTime() {
			return preferences.getLong(REMAIN_TIME_STR, 0);
		}
		/**
		 * 设置这一次番茄周期的剩余时间
		 * @param seconds
		 */
		public void setRemainTime(long seconds) {
			preferences.edit().putLong(REMAIN_TIME_STR, seconds).commit();
		}
		
		//==========
		private static final String CUR_TASK_ID_STR = "current_task_id";
		/**
		 * @return 在番茄钟中的当前task_id，如果没有就是-1
		 */
		public long getCurrentTaskID() {
			return preferences.getLong(CUR_TASK_ID_STR, -1);
		}
		/**
		 * 设置当前番茄钟的对应task_id
		 * @param id，没有对应task就传入-1
		 */
		public void setCurrentTaskID(long id) {
			preferences.edit().putLong(CUR_TASK_ID_STR, id).commit();
		}
	}
	
}
