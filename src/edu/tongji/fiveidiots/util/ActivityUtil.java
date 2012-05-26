/*
 * ActivityUtil类提供所有与Activity相关的工具方法
 * 方法请声明为static
 */

package edu.tongji.fiveidiots.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

public class ActivityUtil {
	
	
	/**
	 * @param from 当前activity对象
	 * @param to 目标activity class对象
	 * @param delayMillis 延迟时间（毫秒）
	 * @param finishSelf 是否结束当前activity
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
}
