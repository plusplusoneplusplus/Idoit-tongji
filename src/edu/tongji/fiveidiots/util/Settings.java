package edu.tongji.fiveidiots.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用来处理用户的设置（配置）
 * @author Andriy
 */
public class Settings {

	/**
	 * 这里放所有默认的设定吧
	 * @author Andriy
	 */
	public static class DefaultSettings {
		/** 默认一个番茄钟时间周期是25min */
		public static final int POMOTIMER_DURATION = 25;
	}
	
	private static final String IDOIT_SETTINGS_STR = "idoit-tongji_settings";
	private final SharedPreferences preferences;
	public Settings(Context context) {
		this.preferences = context.getSharedPreferences(IDOIT_SETTINGS_STR, Context.MODE_PRIVATE);
	}

	//==========
	private static final String POMOTIMER_DURATION_STR = "pomo_timer_duration";
	/** 
	 * @return 一个番茄钟周期的时间，单位分钟
	 */
	public int getPomotimerDuration() {
		return this.preferences.getInt(POMOTIMER_DURATION_STR, DefaultSettings.POMOTIMER_DURATION);
	}
	/**
	 * 设置一个番茄钟周期的时间
	 * @param minute 单位：分钟
	 */
	public void setPomotimerDuration(int minute) {
		this.preferences.edit().putInt(POMOTIMER_DURATION_STR, minute).commit();
	}
	
	//==========
}
