package edu.tongji.fiveidiots.ui;

import java.util.Date;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ui.PomotimerService.PomotimerBinder;
import edu.tongji.fiveidiots.util.TimeUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 番茄计时器
 * @author Andriy
 */
public class PomotimerActivity extends Activity {

	//=====界面相关=====
	private TextView remainTimeTextView;
	private PomotimerCakeView cakeView;
	private ImageView addIdeaImageView;

	/**
	 * 用于从savedInstance中存取计时器状态信息
	 */
	private static final String STATE_STR = "timer_state";
	/**
	 * 用于从savedInstance中存取一个番茄时钟周期的总时间
	 */
	private static final String TOTAL_TIME_STR = "total_time";
	/**
	 * 用于从savedInstance中存取当前番茄周期剩余的时间
	 */
	private static final String REMAIN_TIME_STR = "remain_time";
	/**
	 * 用于决定savedBundle中是否存有state, total_time, remain_time等信息
	 */
	private static final String HAS_TIMER_METRICS_STR = "has_timer_metrics";
	/**
	 * 用于从savedInstance中存取当前对应task的ID
	 */
	private static final String TASK_ID_STR = "task_id";


	//=====消息机制相关=====
	//其他几个消息MSG_XX在PomotimerService里
	private Handler timerHandler;
	/**
	 * 将要退出的消息，通过back键
	 */
	public static final int MSG_WILL_QUIT = 102;	

	/**
	 * 因为onCreate中的savedInstance及intent的bundle中可能包含有用的信息，
	 * 而那时候与service的联系还没有建立起来，所以先存着，
	 * onServiceConnected再set过去
	 * 不等于null即说明内部含有信息
	 */
	private Bundle savedBundle = null;
	
	/**
	 * service是否bind了
	 */
	private boolean serviceBound = false;
	/**
	 * 用来与service交互的binder
	 */
	private PomotimerBinder serviceBinder = null;
	/**
	 * 用于与pomotimer service进行bind的connection
	 */
	private ServiceConnection serviceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceBound = false;
			serviceBinder = null;
			
			Log.d("__ANDRIY__", "service connection disconnected");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceBinder = (PomotimerBinder) service;
			serviceBound = true;
			
			Log.d("__ANDRIY__", "service connection connected");
			
			if (savedBundle != null) {
				//=====之前存了东西了，需要set到service里！=====
				long id = savedBundle.getLong(TASK_ID_STR, -1);
				if (id != -1) {
					//=====不等于-1表明有存id=====
					serviceBinder.setTaskID(id);
				}
				if (savedBundle.getBoolean(HAS_TIMER_METRICS_STR, false)) {
					serviceBinder.setCountingState(savedBundle.getInt(STATE_STR));
					serviceBinder.setTotalTime(savedBundle.getLong(TOTAL_TIME_STR));
					serviceBinder.setRemainTime(savedBundle.getLong(REMAIN_TIME_STR));
				}
				savedBundle = null;
			}
			serviceBinder.init(timerHandler);
			
			//=====UI的显示初始化=====
			refreshTimeLeftText();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		savedBundle = new Bundle();

		//=====得到传入的task的id和name=====
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			savedBundle.putLong(TASK_ID_STR, bundle.getLong(TASK_ID_STR, -1));
			this.setTaskName(bundle.getString("TASK_NAME"));
		}
		else {
			this.setTaskName("这里将显示TASK_NAME");
		}

		//====初始化handler，恢复状态=====
		this.timerHandler = new Handler(new PomotimerCallback());
		if (savedInstanceState != null) {
			/*
			 * 如果横竖屏切换或者其他什么的，可以读取之前的状态
			 * 等到onServiceConnected的时候写入
			 * 写完savedBundle即变为null
			 */
			savedBundle.putBoolean(HAS_TIMER_METRICS_STR, true);
			savedBundle.putInt(STATE_STR, savedInstanceState.getInt(STATE_STR, PomotimerService.STATE_IDLE));
			savedBundle.putLong(TOTAL_TIME_STR, savedInstanceState.getLong(TOTAL_TIME_STR, 0));
			savedBundle.putLong(REMAIN_TIME_STR, savedInstanceState.getLong(REMAIN_TIME_STR, 0));
		}

		//=====设置全屏，但是有标题=====
		//TODO 为了测试，先不全屏
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.timer);
		this.remainTimeTextView = (TextView) findViewById(R.id.remainTimeTextView);
		this.addIdeaImageView = (ImageView) findViewById(R.id.addIdeaImageView);

		//=====中间的大饼图=====
		RelativeLayout cakeViewLayout = (RelativeLayout) findViewById(R.id.cakeViewLayout);
		this.cakeView = new PomotimerCakeView(this);
		cakeViewLayout.addView(this.cakeView);
		
		Log.d("__ANDRIY__", "activity onCreate() finished");
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		this.cakeView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (serviceBinder.getCountingState()) {
				case PomotimerService.STATE_IDLE:
					serviceBinder.resetBySetting();
					//=====有意不break，继续执行吧少年=====
				case PomotimerService.STATE_READY:
					serviceBinder.start();
					Toast.makeText(PomotimerActivity.this, "计时开始！", Toast.LENGTH_SHORT).show();
					break;
				case PomotimerService.STATE_COUNTING:
					//=====计时中，又按了一下，就不鸟它了吧=====
					break;

				default:
					break;
				}
			}
		});
		this.addIdeaImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO 添加奇思妙想之处！
				Toast.makeText(PomotimerActivity.this, "add idea clicked", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onStart() {
		Log.d("__ANDRIY__", "activity onStart()");
		super.onStart();
		//=====bind pomotimer service=====
		Intent serviceIntent = new Intent(this, PomotimerService.class);
		this.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		Log.d("__ANDRIY__", "activity onDestroy()");
		//=====unbind pomotimer service=====
		if (serviceBound) {
			this.unbindService(serviceConnection);
			serviceBound = false;
		}

		super.onDestroy();
	}

	/**
	 * 如果横竖屏什么的，可以存储当前的状态，供之后读取
	 * @param outState
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (serviceBound) {
			outState.putInt(STATE_STR, serviceBinder.getCountingState());
			outState.putLong(TOTAL_TIME_STR, serviceBinder.getTotalTime());
			outState.putLong(REMAIN_TIME_STR, serviceBinder.getRemainTime());
			Log.d("__ANDRIY__", "activity onSaveInstance()");
//			serviceBinder.stop();
		}
	}

	@Override
	public void onBackPressed() {
		if (serviceBound && serviceBinder.isCounting()) {
			//=====正在counting呢，哪能说退就退=====
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(R.string.PT_quit_title);
			builder.setMessage(R.string.PT_quit_message);
			builder.setPositiveButton(R.string.PT_quit_confirm, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Message msg = Message.obtain(timerHandler, MSG_WILL_QUIT);
					msg.sendToTarget();
				}
			});
			builder.setNegativeButton(R.string.PT_quit_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.create().show();
			return;
		}

		super.onBackPressed();
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
		if (!serviceBound) {
			//=====还没有连接，也显示不了什么=====
			return;
		}

		switch (serviceBinder.getCountingState()) {
		case PomotimerService.STATE_IDLE:
			this.remainTimeTextView.setText(TimeUtil.parseDateTime(new Date()));
			break;

		case PomotimerService.STATE_READY:
			this.remainTimeTextView.setText("Ready? Go!");
			break;

		case PomotimerService.STATE_COUNTING:
			this.remainTimeTextView.setText("剩余：" + TimeUtil.parseRemainingTime(serviceBinder.getRemainTime()));
			break;

		default:
			break;
		}
	}

	/**
	 * 当倒计时更新的时候，刷新中央的大饼图
	 */
	private void refreshCakeView() {
		this.cakeView.invalidate();
	}
	
	

	/**
	 * 处理番茄计时器相关的消息
	 * 处理了番茄周期结束、剩余时间改变
	 * @author Andriy
	 */
	private class PomotimerCallback implements Callback {

		@Override
		public boolean handleMessage(Message msg) {
			boolean msgHandled = false;
			switch (msg.what) {
			case PomotimerService.MSG_TIMES_UP:
				refreshCakeView();
				refreshTimeLeftText();
				msgHandled = true;
				break;

			case PomotimerService.MSG_REMAIN_TIME_CHANGED:
				refreshTimeLeftText();
				refreshCakeView();
				msgHandled = true;
				break;
				
			case MSG_WILL_QUIT:
				if (serviceBound && serviceBinder.isCounting()) {
					serviceBinder.stop();
					serviceBinder.notifyInterrupted();
					refreshCakeView();
					refreshTimeLeftText();
				}
				onBackPressed();
				msgHandled = true;
				break;

			default:
				msgHandled = false;
			}
			return msgHandled;
		}
	}

	/**
	 * 中间的大饼图
	 * @author Andriy
	 */
	private class PomotimerCakeView extends View {

		public PomotimerCakeView(Context context) {
			super(context);
			
			//=====获取屏幕信息、决定大饼图的大小=====
			DisplayMetrics metrics = new DisplayMetrics();
			PomotimerActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int length = metrics.widthPixels < metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
			int radius = (int) (0.5 * 0.7 * length);	//对于短边组成的正方形，圆的半径取其一半的7/10
			int midWidth = metrics.widthPixels >> 1;
			int midHeight = metrics.heightPixels >> 1;
			boundRect = new RectF(midWidth - radius, midHeight -radius, midWidth + radius, midHeight + radius);
		}
		
		/**
		 * 用于在后来画扇形的时候传入框定圆大小
		 */
		private final RectF boundRect;

		private final int AVAILABLE_COLOR = Color.rgb(0, 200, 100);
		private final int CONSUMED_COLOR = Color.rgb(100, 100, 100);

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if (!serviceBound) {
				//=====service都没有连上，画什么呀，洗洗睡吧=====
				return;
			}

			//=====根据当前计时器状态不同重绘=====
			switch (serviceBinder.getCountingState()) {
			case PomotimerService.STATE_IDLE:
				this.onDrawIdle(canvas);
				break;
			case PomotimerService.STATE_READY:
				this.onDrawReady(canvas);
				break;
			case PomotimerService.STATE_COUNTING:
				this.onDrawCounting(canvas);
				break;

			default:
				break;
			}
		}

		/**
		 * 当IDLE状态时onDraw
		 * @param canvas
		 */
		private void onDrawIdle(Canvas canvas) {
			Paint paint = new Paint();
			paint.setDither(true);
			paint.setColor(AVAILABLE_COLOR);
			canvas.drawArc(boundRect, -90, 360, true, paint);
		}
		
		/**
		 * 当READY状态时onDraw
		 * @param canvas
		 */
		private void onDrawReady(Canvas canvas) {
			this.onDrawIdle(canvas);
		}
		
		/**
		 * 当COUNTING状态时onDraw
		 * @param canvas
		 */
		private void onDrawCounting(Canvas canvas) {
			Paint paint = new Paint();
			paint.setDither(true);
			
			long total = serviceBinder.getTotalTime();
			long remain = serviceBinder.getRemainTime();

			paint.setColor(CONSUMED_COLOR);
			float startAngle = -90;
			float sweepAngle = 360 * (total - remain) / total;
			canvas.drawArc(boundRect, startAngle, sweepAngle, true, paint);

			paint.setColor(AVAILABLE_COLOR);
			startAngle = 360 * (total - remain) / total - 90;
			sweepAngle = 360 * remain / total;
			canvas.drawArc(boundRect, startAngle, sweepAngle, true, paint);
		}
	}
}
