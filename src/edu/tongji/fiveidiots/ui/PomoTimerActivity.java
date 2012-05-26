package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 番茄计时器
 * 
 * @author Andriy
 */
public class PomoTimerActivity extends Activity {

	private TextView timeLeftTextView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//设置全屏，但是有标题
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.timer);

		timeLeftTextView = (TextView) findViewById(R.id.timeLeftTextView);


		//TODO
		this.setTaskName("这里将显示任务名称");
		this.setTimeLeft("这里将显示倒计时");
	}

	/**
	 * 将任务的名字显示在界面上
	 * 初步决定放在title栏
	 * @param name
	 */
	public void setTaskName(String name) {
		this.setTitle(name);
	}

	/**
	 * 将倒计时显示在屏幕上
	 * 初步决定放在右下角 
	 * TODO @param time 是放一个string还是传进来long还没有决定
	 */
	public void setTimeLeft(String time) {
		if (this.timeLeftTextView != null) {
			this.timeLeftTextView.setText(time);			
		}
	}
}
