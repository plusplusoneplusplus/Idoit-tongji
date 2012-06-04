package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ui.PomotimerService.PomotimerBinder;
import edu.tongji.fiveidiots.util.ActivityUtil;
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
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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

	//=====消息机制相关=====
	//=====其他几个消息MSG_XX在PomotimerService里=====
	private Handler timerHandler;
	/** 将要退出的消息，通过back键 */
	public static final int MSG_WILL_QUIT = 102;	
	
	/**
	 * 当前对应的task_id，如果没有对应task，就是-1
	 */
	private long currentTaskID = -1;
	
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
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serviceBinder = (PomotimerBinder) service;
			serviceBound = true;
			serviceBinder.setTaskID(currentTaskID);
			serviceBinder.init(timerHandler);
			
			//=====UI的显示初始化=====
			refreshRemainTimeText(PomotimerService.SECTION_POMO);
			refreshCakeView();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//=====恢复状态、初始化Handler=====
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			this.currentTaskID = bundle.getLong(OverviewTaskListActivity.TASK_ID_STR, -1); 
			this.setTaskName(bundle.getString(OverviewTaskListActivity.TASK_NAME_STR));
		}
		else {
			this.setTaskName("这里将显示TASK_NAME");
		}
		this.timerHandler = new Handler(new PomotimerCallback());

		//=====设置全屏，但是有标题=====
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.timer);
		this.remainTimeTextView = (TextView) findViewById(R.id.remainTimeTextView);
		this.addIdeaImageView = (ImageView) findViewById(R.id.addIdeaImageView);

		//=====中间的大饼图=====
		RelativeLayout cakeViewLayout = (RelativeLayout) findViewById(R.id.cakeViewLayout);
		this.cakeView = new PomotimerCakeView(this);
		cakeViewLayout.addView(this.cakeView);
	}

	@Override
	protected void onResume() {
		super.onResume();

		this.cakeView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (serviceBinder.getCountingState()) {
				case PomotimerService.STATE_IDLE:
					//serviceBinder.resetBySetting();
					//=====有意不break，继续执行吧少年=====
				case PomotimerService.STATE_READY:
					serviceBinder.start();
					Toast.makeText(PomotimerActivity.this, "计时开始！", Toast.LENGTH_SHORT).show();
					break;
				case PomotimerService.STATE_COUNTING: {
					int section = serviceBinder.getCurrentSection();
					switch (section) {
					case PomotimerService.SECTION_SHORTBREAK:
						showStopTimerDialog(R.string.PT_break_quit_title, R.string.PT_break_quit_message, 
								R.string.PT_break_quit_confirm, R.string.PT_break_quit_cancel, new onStopTimer() {
									
									@Override
									public void onStop() {
										serviceBinder.setRemainTime(0);
									}
								});
						break;
					default:
						//=====番茄计时中，又按了一下，就不鸟它了吧=====
						break;
					}
					
				}
					break;

				default:
					break;
				}
			}
		});
		this.addIdeaImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//=====进入新任务建立的界面=====
				Bundle bundle = new Bundle();
				bundle.putLong(OverviewTaskListActivity.TASK_ID_STR, -1);
				ActivityUtil.startActivityWithBundle(PomotimerActivity.this, TaskDetailsActivity.class, 0, false, bundle);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		//=====bind pomotimer service=====
		Intent serviceIntent = new Intent(this, PomotimerService.class);
		this.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		//=====unbind pomotimer service=====
		if (serviceBound) {
			this.unbindService(serviceConnection);
			serviceBound = false;
		}

		super.onDestroy();
	}

	interface onStopTimer {
		public void onStop();
	}
	
	/**
	 * 当想要暂停计时器时，显示的dialog
	 * msg 按确定暂停后，发送的消息
	 */
	private void showStopTimerDialog(int titleID, int msgID, int confirmId, int cancleId, final onStopTimer st) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(titleID);
		builder.setMessage(msgID);
		builder.setPositiveButton(confirmId, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				st.onStop();
			}
		});
		builder.setNegativeButton(cancleId, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
	
	
	@Override
	public void onBackPressed() {
		if (serviceBound && serviceBinder.isCounting()) {
			if (serviceBinder.getCurrentSection() == PomotimerService.SECTION_POMO) {
				//=====番茄正在counting呢，哪能说退就退=====
				showStopTimerDialog(R.string.PT_pomo_quit_title, R.string.PT_pomo_quit_message, 
						R.string.PT_pomo_quit_confirm, R.string.PT_pomo_quit_cancel, new onStopTimer() {

							@Override
							public void onStop() {
								Message msg = Message.obtain(timerHandler, MSG_WILL_QUIT);
								msg.arg1 = serviceBinder.getCurrentSection();
								msg.sendToTarget();
							}
					
				});
				
				return;
			}
			else if (serviceBinder.getCurrentSection() == PomotimerService.SECTION_SHORTBREAK) {
				//=====休息正在counting呢，哪能说退就退=====
				showStopTimerDialog(R.string.PT_break_quit_title, R.string.PT_break_quit_message, 
						R.string.PT_break_quit_confirm, R.string.PT_break_quit_cancel, new onStopTimer() {

							@Override
							public void onStop() {
								Message msg = Message.obtain(timerHandler, MSG_WILL_QUIT);
								msg.arg1 = serviceBinder.getCurrentSection();
								msg.sendToTarget();
							}
					
				});
				
				return;
			}
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
	 * 参数表示当前处于什么阶段，详见阶段定义
	 */
	private void refreshRemainTimeText(int section) {
		if (!serviceBound) {
			//=====还没有连接，也显示不了什么=====
			return;
		}
		
		String str = TimeUtil.parseRemainingTime(serviceBinder.getTotalTime());
		switch (section) {
		case PomotimerService.SECTION_POMO:
			str = "此次番茄时钟周期共：" + str;
			break;
		case PomotimerService.SECTION_SHORTBREAK:
		case PomotimerService.SECTION_LONGBREAK:
			str = "此次休息时间共：" + str;
			break;
		default:
			break;
		}
		
		
		switch (serviceBinder.getCountingState()) {
		case PomotimerService.STATE_IDLE:
		case PomotimerService.STATE_READY:
			this.remainTimeTextView.setText(str);
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
				refreshRemainTimeText(msg.arg1);
				msgHandled = true;
				break;

			case PomotimerService.MSG_REMAIN_TIME_CHANGED:
				refreshRemainTimeText(msg.arg1);
				refreshCakeView();
				msgHandled = true;
				break;
				
			case MSG_WILL_QUIT:
				if (serviceBound && serviceBinder.isCounting()) {
					serviceBinder.stop();
					serviceBinder.notifyInterrupted();
					refreshCakeView();
					refreshRemainTimeText(msg.arg1);
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
