package edu.tongji.fiveidiots.ui;

import java.util.Timer;
import java.util.TimerTask;

import edu.tongji.fiveidiots.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
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

	//=====界面相关=====
	private TextView timeLeftTextView;
	private Button testButton;
	
	//=====计时时间参数相关=====
	private static final String TOTAL_TIME_STR = "total_time";
	private static final String LEFT_TIME_STR = "left_time";
	private long totalTime = 0;
	private long leftTime = 0;

	//=====消息机制相关=====
	private Handler timerHandler;
	private static final int MSG_TIMES_UP = 100;
	private static final int MSG_TIME_LEFT_CHANGED = 101;

	//=====计时的状态和timertask相关=====
	private TimerTask countingTimerTask;
	private int countingState = STATE_IDLE;
	private static final String STATE_STR = "timer_state";
	private static final int STATE_IDLE = 1;
	private static final int STATE_READY = 2;
	private static final int STATE_COUNTING = 3;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//=====设置全屏，但是有标题=====
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.timer);

		this.timerHandler = new Handler(new PomotimerCallback());
		if (savedInstanceState != null) {
			//=====如果横竖屏切换或者其他什么的，可以读取之前的状态=====
			countingState = savedInstanceState.getInt(STATE_STR, STATE_IDLE);
			totalTime = savedInstanceState.getLong(TOTAL_TIME_STR, 0);
			leftTime = savedInstanceState.getLong(LEFT_TIME_STR, 0);
		}

		this.timeLeftTextView = (TextView) findViewById(R.id.timeLeftTextView);

		//=====测试中=====
		this.testButton = (Button) findViewById(R.id.testButton);
		this.testButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (countingState != STATE_READY) {
					resetTimer(10, 10);
				}
				startTimer();
			}
		});
		//=====测试代码完毕=====
		
		this.setTaskName("这里将显示任务名称");

		//=====计时器的初始化or重建
		switch (countingState) {
		case STATE_IDLE:
		case STATE_READY:
			break;
		case STATE_COUNTING:
			//=====resume上一次计时=====
			this.resetTimer(totalTime, leftTime);
			this.startTimer();
			break;
		default:
			break;
		}
	}

	/**
	 * 如果横竖屏什么的，可以存储当前的状态，供之后读取
	 * @param outState
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(STATE_STR, countingState);
		outState.putLong(TOTAL_TIME_STR, totalTime);
		outState.putLong(LEFT_TIME_STR, leftTime);
		this.releaseTimer();
	}

	/**
	 * 初始化计时器
	 * @param total 一共多少秒
	 */
	private void resetTimer(long total, long left) {
		this.releaseTimer();
		this.totalTime = total;
		this.leftTime = left;
		this.countingTimerTask = new TimerTask() {
			
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
		this.countingState = STATE_READY;
	}
	
	/**
	 * 释放计时器及其相应的timertask
	 */
	private void releaseTimer() {
		if (countingTimerTask != null) {
			countingTimerTask.cancel();
			countingTimerTask = null;
		}
		this.countingState = STATE_IDLE;
	}
	
	/**
	 * 开始计时器
	 */
	private void startTimer() {
		new Timer().scheduleAtFixedRate(countingTimerTask, 0, 1000);
		this.countingState = STATE_COUNTING;
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
		//Canvas
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
