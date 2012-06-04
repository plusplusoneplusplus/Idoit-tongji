package edu.tongji.fiveidiots.ui;

import java.util.Timer;
import java.util.TimerTask;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ctrl.TaskController;
import edu.tongji.fiveidiots.util.Settings;
import edu.tongji.fiveidiots.util.TimeUtil;
import edu.tongji.fiveidiots.util.Settings.TimerTempSettings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

/**
 * 番茄钟的Service，运行在后台
 * ①单个番茄时间周期的管理，开始、计时、中断、结束
 * ②多个番茄时间周期之间的管理，休息、长暂停等等
 * 因此只需bound态足以，无需started态
 * @author Andriy
 */
public class PomotimerService extends Service {

	private final PomotimerBinder binder = new PomotimerBinder();

	//=====计时时间参数相关=====
	private long totalTime = 0;
	private long remainTime = 0;

	//=====计时的状态和timertask相关=====
	public static final int STATE_IDLE = 1;
	public static final int STATE_READY = 2;
	public static final int STATE_COUNTING = 3;
	private int countingState = STATE_IDLE;
	private TimerTask countingTimerTask;

	//=====消息机制相关=====
	private Handler handler;
	/** 时间到了，此次番茄时钟周期finish */
	public static final int MSG_TIMES_UP = 100;
	/** 此次番茄时钟周期剩余时间变化了 */
	public static final int MSG_REMAIN_TIME_CHANGED = 101;

	//=====与notification相关=====
	private static final int POMO_NOTIFICATION_ID = 92837;
	
	//=====task相关的信息=====
	private long taskID = -1;
	

	//=====状态：表示是番茄时间阶段，还是短/长休息时间阶段=====
	public static final int SECTION_POMO = 1000;
	public static final int SECTION_SHORTBREAK = 1001;
	public static final int SECTION_LONGBREAK = 1002;
	/**
	 * currentSection表示当前所处阶段
	 * 如果当前倒计时处于暂停，则表示下一个将要开始的阶段
	 * 如果倒计时处于执行中，则表示当前阶段
	 * @author IRainbow5
	 */
	private int currentSection = SECTION_POMO;

	@Override
	public void onCreate() {
		super.onCreate();
		this.readFromSettings();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return this.binder;
	}

	@Override
	public void onDestroy() {
		/**
		 * 只有在运行番茄时间是，才将计时器各项值写入sharepreference
		 */
		if (this.currentSection == SECTION_POMO) {
			this.saveIntoSettings();
		}

		this.releaseTimer();
		this.cancelNotification();
		this.handler = null;

		super.onDestroy();
	}

	/**
	 * 初始化计时器的状态及相关变量
	 */
	private void initState() {
		Settings settings = new Settings(PomotimerService.this);
		int duration = 1;
		
		switch (currentSection) {
		case SECTION_POMO:
			duration = settings.getPomotimerDuration();
			break;
		case SECTION_SHORTBREAK:
			duration = settings.getPomotimerInterval();
			break;
		case SECTION_LONGBREAK:
			duration = settings.getPomotimerLongInterval();
			break;
			
		default:
			break;
		}
		
		switch (countingState) {
		case STATE_IDLE:
			resetTimer(duration*60, duration*60);
			break;
		case STATE_READY:
			//=====需要重置一下timertask，比如=====
			resetTimer(totalTime, remainTime);
			break;
		case STATE_COUNTING:
			//=====resume上一次计时=====
			resetTimer(totalTime, remainTime);
			startTimer();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 将当前的参数存到Settings中
	 */
	private void saveIntoSettings() {
		TimerTempSettings settings = new Settings(this).getTimerSettings();
		settings.setTimerState(this.countingState);
		settings.setTotalTime(this.totalTime);
		settings.setRemainTime(this.remainTime);
		settings.setCurrentTaskID(this.taskID);
	}
	
	/**
	 * 从Settings中读出之前存好的参数
	 */
	private void readFromSettings() {
		TimerTempSettings settings = new Settings(this).getTimerSettings();
		this.countingState = settings.getTimerState();
		this.totalTime = settings.getTotalTime();
		this.remainTime = settings.getRemainTime();
		this.taskID = settings.getCurrentTaskID();
	}

	/**
	 * 初始化计时器
	 * @param total 一共多少秒
	 */
	private void resetTimer(long total, long remain) {
		this.releaseTimer();
		this.totalTime = total;
		this.remainTime = remain;

		//=====TODO 仅为测试用
		//this.remainTime = 10;
		this.remainTime = 20;
		//=====END
		
		this.countingTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				if (remainTime <= 0) {
					
					releaseTimer();
					
					String str = "此次番茄周期结束！";
					switch(currentSection) {
					case SECTION_POMO:
						//=====通知一个task完成了此次番茄周期=====
						TaskController controller = new TaskController(PomotimerService.this);
						controller.FinishCycle(taskID, 0, (int) totalTime / 60);
						str = "此次番茄周期结束！";
						break;
					case SECTION_SHORTBREAK:
						str = "休息结束！";
						break;
					case SECTION_LONGBREAK:
						str = "长休息结束！";
						break;
					default:
						break;
					}
					showNotification(str, true, false);
					
					/**
					 * 我认为service的totaltime等值应该是自己独立变化
					 * 而不是依靠activity的click事件
					 * 因此我在这里重置了totaltime等值
					 * @author IRainbow5
					 */
					changeState();
					
					if (handler != null) {
						Message msg = Message.obtain(handler, MSG_TIMES_UP);
						msg.arg1 = currentSection;
						msg.sendToTarget();
					}
					
				}
				else {
					remainTime--;
					showNotification("剩余："+TimeUtil.parseRemainingTime(remainTime), false, true);
					if (handler != null) {
						Message msg = Message.obtain(handler, MSG_REMAIN_TIME_CHANGED);
						msg.arg1 = currentSection;
						msg.sendToTarget();
					}
				}
			}

		};
		this.countingState = STATE_READY;
	}
	
	/**
	 * 根据当前阶段改变新阶段
	 * 读取新的计时器相关数据
	 */
	private void changeState() {
		/**
		 * 修改所处阶段
		 */
		if(currentSection == SECTION_POMO) {
			currentSection = SECTION_SHORTBREAK;
		}
		else {
			currentSection = SECTION_POMO;
		}
		
		initState();
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
		this.stopForeground(true);
		
	}

	/**
	 * 开始计时器
	 */
	private void startTimer() {
		new Timer().scheduleAtFixedRate(countingTimerTask, 0, 1000);
		this.countingState = STATE_COUNTING;
		
		/**
		 * 判断处于哪个阶段
		 */
		String str = "番茄周期开始！";
		switch(currentSection) {
		case SECTION_POMO:
			str = "番茄周期开始！";
			break;
		case SECTION_SHORTBREAK:
			str = "休息时间开始！";
			break;
		case SECTION_LONGBREAK:
			str = "长休息时间开始！";
			break;
		default:
			break;
		}
		
		Notification notification = new Notification(R.drawable.icon_idoit, null, System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, PomotimerActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,	notificationIntent, 0);
		notification.setLatestEventInfo(this, "IDoit",  str, pendingIntent);
		this.startForeground(POMO_NOTIFICATION_ID, notification);
	}

	/**
	 * 在通知栏上显示一个notification
	 * @param message 要显示的内容
	 * @param attention 是否要引起注意，如声音、震动、光
	 * @param ongoing 是否正在进行中
	 */
	private void showNotification(String message, boolean attention, boolean ongoing) {
		Notification notification = new Notification(R.drawable.icon_idoit, null, System.currentTimeMillis());
		if (attention) {
			Settings settings = new Settings(this);
			if(settings.getPomotimerNotifyVibrate()) {
				notification.defaults |= Notification.DEFAULT_VIBRATE;
			}
			notification.sound = settings.getPomotimerNotifyRingTone();
		}
		if (ongoing) {
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
			notification.flags |= Notification.FLAG_NO_CLEAR;
		}
		else {
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
		}
		
		Intent notificationIntent = new Intent(this, PomotimerActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, 	notificationIntent, 0);
		notification.setLatestEventInfo(this, "IDoit", message,	contentIntent);

		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(POMO_NOTIFICATION_ID, notification);
	}
	
	/**
	 * 清楚在通知栏上的notification
	 */
	private void cancelNotification() {
		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(POMO_NOTIFICATION_ID);
	}


	/**
	 * 用于service的client和server端进行交互
	 * @author Andriy
	 */
	public class PomotimerBinder extends Binder {
		
		/**
		 * @return 当前的计时器状态 （STATE_IDLE, STATE_READY, STATE_COUNTING）
		 */
		public int getCountingState() {
			return countingState;
		}
		
		/**
		 * @return 当前计时器状态是否为STATE_COUNTING
		 */
		public boolean isCounting() {
			return countingState == STATE_COUNTING;
		}
		
		/**
		 * @return 此次番茄时钟周期的总时间
		 */
		public long getTotalTime() {
			return totalTime;
		}
		
		/**
		 * @return 此次番茄时钟周期的剩余时间
		 */
		public long getRemainTime() {
			return remainTime;
		}
		
		/**
		 * 设置当前对应task的ID
		 * @param id
		 */
		public void setTaskID(long id) {
			taskID = id;
		}

		/**
		 * 让service的计时器start
		 */
		public void start() {
			startTimer();
		}

		/**
		 * 重置计时器，total和remain是根据Settings里的设置而设定的
		 */
		/**
		 * 觉得重置设置应该有service自己干，而不是前台发消息让他干，因此注释了
		 * @author IRainbow5
		 */
		//public void resetBySetting() {
			//int duration = new Settings(PomotimerService.this).getPomotimerDuration();
			//resetTimer(duration*60, duration*60);
		//}
		
		/**
		 * 停止计时器，释放资源
		 */
		public void stop() {
			releaseTimer();
		}
		
		/**
		 * 告知发生了中断
		 * 因为可能有延迟，需要重新check一遍
		 */
		public void notifyInterrupted() {
			if (taskID == -1) {
				//=====是新任务或者没有对应任务，直接return=====
				return;
			}

			TaskController controller = new TaskController(PomotimerService.this);
			int pastTime = (int)(totalTime - remainTime) / 60;
			controller.InterruptTask(taskID, pastTime);
		}
		
		/**
		 * 计时器整个工作状态的初始化
		 */
		public void init(Handler aHandler) {
			handler = aHandler;
			initState();
		}
		
		/**
		 * 获取当前阶段
		 * PS:真不想写这个api，有待重构
		 */
		public int getCurrentSection() {
			return currentSection;
		}

		public void setRemainTime(int t) {
			remainTime = t;
		}
	}

}
