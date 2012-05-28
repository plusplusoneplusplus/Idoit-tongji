package edu.tongji.fiveidiots.ui;

import java.util.Timer;
import java.util.TimerTask;

import edu.tongji.fiveidiots.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 番茄计时器
 * @author Andriy
 */
public class PomoTimerActivity extends Activity {

	private TextView timeLeftTextView;
	private Button testButton;
	
	private long totalTime = 0;
	private long leftTime = 0;

	private Handler timerHandler;
	private static final int MSG_TIMES_UP = 100;
	private static final int MSG_TIME_LEFT_CHANGED = 101;

	private TimerTask countingTimerTask;
	private TimerState countingState = TimerState.IDLE;
	private enum TimerState {
		IDLE, READY, COUNTING
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//设置全屏，但是有标题
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.timer);

		this.timerHandler = new Handler(new PomotimerCallback());

		this.timeLeftTextView = (TextView) findViewById(R.id.timeLeftTextView);
		this.testButton = (Button) findViewById(R.id.testButton);
		this.testButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (countingState != TimerState.READY) {
					resetTimer(10);
				}
				startTimer();
			}
		});
		
		this.setTaskName("这里将显示任务名称");
		this.resetTimer(10);
		
		/*
		 * TODO 有bug，横屏竖屏一变换，会重新启动activity，onCreate重新调用一次，于是重新开始了，
		 * 需要另外记录状态，也许可以在onConfigurationChanged()那里，在退出的时候写入preferences，
		 * 在创建的时候从那里读看下有没有上次的数据。
		 */
	}

	/**
	 * 初始化计时器
	 * @param total 一共多少秒
	 */
	private void resetTimer(long total) {
		this.releaseTimer();
		this.leftTime = this.totalTime = total;
		countingTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				if (leftTime <= 0) {
					Message msg = Message.obtain(timerHandler, MSG_TIMES_UP);
					msg.sendToTarget();
					return;
				}

				leftTime--;
				Message msg = Message.obtain(timerHandler, MSG_TIME_LEFT_CHANGED);
				msg.sendToTarget();
			}
		};
		this.countingState = TimerState.READY;
	}
	
	/**
	 * 释放计时器及其相应的timertask
	 */
	private void releaseTimer() {
		if (countingTimerTask != null) {
			countingTimerTask.cancel();
			countingTimerTask = null;
		}
		this.countingState = TimerState.IDLE;
	}
	
	/**
	 * 开始计时器
	 */
	private void startTimer() {
		new Timer().scheduleAtFixedRate(countingTimerTask, 0, 1000);
		this.countingState = TimerState.COUNTING;
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
	private void refreshTimeLeftText() {
		this.timeLeftTextView.setText("剩余：" + this.leftTime);
		//TODO 格式化字符串
	}

	
	/**
	 * 当倒计时更新的时候，刷新中央的大饼图
	 */
	private void refreshCakeView() {
		//TODO
	}

	/**
	 * 处理计时器内的消息的callback
	 * 处理了番茄周期结束、剩余时间改变
	 * @author Andriy
	 */
	private class PomotimerCallback implements Callback {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TIMES_UP:
				releaseTimer();
				Toast.makeText(PomoTimerActivity.this, "timer stoped", Toast.LENGTH_SHORT).show();
				return true;

			case MSG_TIME_LEFT_CHANGED:
				refreshTimeLeftText();
				refreshCakeView();
				return true;

			default:
				break;
			}
			return false;
		}		
	}

}
