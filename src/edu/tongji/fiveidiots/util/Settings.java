package edu.tongji.fiveidiots.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用来处理用户的设置（配置）
 * @author Andriy
 */
public class Settings {

	private static final String IDOIT_SETTINGS_STR = "idoit-tongji_settings";
	
	private final SharedPreferences preferences;
	public Settings(Context context) {
		this.preferences = context.getSharedPreferences(IDOIT_SETTINGS_STR, Context.MODE_PRIVATE);
	}

	//这么用！
//	private static final String POWER_ON = "setting_power_on";
//	public boolean isPowerOn() {
//		return this.preferences.getBoolean(POWER_ON, false);
//	}
//	public void setPowerOn(boolean powerOn) {
//		this.preferences.edit().putBoolean(POWER_ON, powerOn).commit();
//	}
}
