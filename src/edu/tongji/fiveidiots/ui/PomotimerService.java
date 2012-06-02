package edu.tongji.fiveidiots.ui;

import java.util.Timer;
import java.util.TimerTask;

import edu.tongji.fiveidiots.R;
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
		this.saveIntoSettings();

		this.releaseTimer();
		this.cancelNotification();
		this.handler = null;

		super.onDestroy();
	}

	/**
	 * 初始化计时器的状态及相关变量
	 */
	private void initState() {
		int duration = new Settings(PomotimerService.this).getPomotimerDuration();
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
		this.remainTime = 10;
		//=====END
		
		this.countingTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				if (remainTime <= 0) {
					releaseTimer();
					showNotification("此次番茄周期结束！", true, false);
					if (handler != null) {
						Message msg = Message.obtain(handler, MSG_TIMES_UP);
						msg.sendToTarget();
					}
				}
				else {
					remainTime--;
					showNotification("剩余："+TimeUtil.parseRemainingTime(remainTime), false, true);
					if (handler != null) {
						Message msg = Message.obtain(handler, MSG_REMAIN_TIME_CHANGED);
						msg.sendToTarget();
					}
				}
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
		this.stopForeground(true);
	}

	/**
	 * 开始计时器
	 */
	private void startTimer() {
		new Timer().scheduleAtFixedRate(countingTimerTask, 0, 1000);
		this.countingState = STATE_COUNTING;
		
		Notification notification = new Notification(R.drawable.icon, null, System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, PomotimerActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,	notificationIntent, 0);
		notification.setLatestEventInfo(this, "IDoit", 	"番茄周期开始！", pendingIntent);
		this.startForeground(POMO_NOTIFICATION_ID, notification);
	}

	/**
	 * 在通知栏上显示一个notification
	 * @param message 要显示的内容
	 * @param attention 是否要引起注意，如声音、震动、光
	 * @param ongoing 是否正在进行中
	 */
	private void showNotification(String message, boolean attention, boolean ongoing) {
		Notification notification = new Notification(R.drawable.icon, null, System.currentTimeMillis());
		if (attention) {
			notification.defaults |= Notification.DEFAULT_ALL;
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
		public void resetBySetting() {
			int duration = new Settings(PomotimerService.this).getPomotimerDuration();
			resetTimer(duration*60, duration*60);
		}
		
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
			// TODO 告诉XXX这个taskID的任务中断了！
			//taskID
		}
		
		/**
		 * 计时器整个工作状态的初始化
		 */
		public void init(Handler aHandler) {
			handler = aHandler;
			initState();
		}
	}

}
