package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.Toast;
import android.widget.Chronometer.OnChronometerTickListener;

/**
 * 番茄计时器
 * 
 * @author Andriy
 */
public class PomoTimerActivity extends Activity {

	private Chronometer timer = null;
	private long timeTotalInS = 0;
	private long timeLeftInS = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//设置全屏，但是有标题
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.timer);

		timer = (Chronometer) findViewById(R.id.timer);
		this.initTimer(10);
		this.setTaskName("这里将显示任务名称");
		timer.start();
		
		//TODO 有bug，横屏竖屏一变换，onCreate重新调用一次，于是重新开始了，需要另外记录状态
	}

	/**
	 * 初始化计时器，计时器是通过widget.Chronometer来实现的
	 * @param total 一共多少秒
	 */
	private void initTimer(long total) {
		this.timeTotalInS = total;
		this.timeLeftInS = total;
		timer.setOnChronometerTickListener(new OnChronometerTickListener() {
			
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				if (timeLeftInS <= 0) {
					Toast.makeText(PomoTimerActivity.this, "timer stoped", Toast.LENGTH_SHORT).show();
					timer.stop();
					return;
				}

				timeLeftInS--;
				refreshTimeLeft();
				refreshCakeView();
			}
		});
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
	 */
	private void refreshTimeLeft() {
		this.timer.setText("剩余：" + timeLeftInS);
		//TODO 格式化字符串
	}

	
	/**
	 * 当倒计时更新的时候，刷新中央的大饼图
	 */
	private void refreshCakeView() {
		//TODO
	}

}
