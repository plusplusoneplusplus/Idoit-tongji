/*
 * ActivityUtil类提供所有与Activity相关的工具方法
 * 方法请声明为static
 */

package edu.tongji.fiveidiots.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ActivityUtil {
	
	
	/**
	 * @param from 当前activity对象
	 * @param to 目标activity class对象
	 * @param delayMillis 延迟时间（毫秒）
	 * @param finishSelf 是否结束当前activity
	 * @author IRainbow5
	 */
	public static void startNewActivity(final Activity from, final Class<?> to, long delayMillis, final boolean finishSelf){
		
		Handler handler = new Handler();
		Runnable r = new Runnable(){
			@Override
			public void run() {
				Intent intent = new Intent(from, to);
				from.startActivity(intent);
				if(finishSelf == true)
					from.finish();
			}
		};
		handler.postDelayed(r, delayMillis);
	}
	
	/**
	 * 开始另一个activity，并且在它们之间传递一个bundle，（带有数据）
	 * @param from 从哪个activity
	 * @param to 到那个activity
	 * @param delayMillis 延迟多久
	 * @param finishSelf 原activity是否finish()
	 * @param bundle 带有数据的bundle
	 * @author Andriy
	 */
	public static void startActivityWithBundle(final Activity from, final Class<?> to, 
			long delayMillis, final boolean finishSelf, final Bundle bundle) {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent(from, to);
				intent.putExtras(bundle);
				from.startActivity(intent);
				if(finishSelf == true) {
					from.finish();
				}
			}
		}, delayMillis);
	}
}
