package edu.tongji.fiveidiots.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ctrl.TaskInfo;
import edu.tongji.fiveidiots.util.ActivityUtil;
import edu.tongji.fiveidiots.util.Settings;
import edu.tongji.fiveidiots.util.TestingHelper;
import edu.tongji.fiveidiots.util.TimeUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * 生成、初始化任务细节部分的UI
 * 为什么会单独弄一个Helper类出来，因为之前代码在内部类里太多太乱了！
 * @author Andriy
 */
public class TaskDetailViewHelper {
	/**
	 * 展示这个task的所有细节
	 */
	private final TaskInfo task;
	/**
	 * 此任务的前驱任务
	 */
	private TaskInfo previousTask;
	/**
	 * 此任务的后续任务
	 */
	private TaskInfo followingTask;

	private final Context context;
	public TaskDetailViewHelper(Context aContext, TaskInfo aTask) {
		this.context = aContext;
		this.task = aTask;
		
		//=====TODO 测试中=====
		this.task.addTag("hello");
		this.task.addTag("world");
		Date date = new Date(2012,12,5);
		this.task.setDeadline(date);
		this.task.setDeadline(new Date(new Date().getTime() - 10000));
		this.task.setStarttime(new Date());
//		this.task.setUnfinishedCycle(60);
		this.task.setFinishedCycle(101);
		
		this.previousTask = TestingHelper.getRandomTask();
		this.previousTask.setName("PREV: " + this.previousTask.getName());
		this.followingTask = null;
	}

	//=====第一页=====
	/** 名称 */
	private EditText taskNameText;
	/** 备注 */
	private EditText taskMemoText;
	/** 周期信息 */
	private TextView periodicInfoText;
	/** 开始时间 */
	private TextView startTimeText;
	/** 截止时间 */
	private TextView deadlineText;
	/** 优先级 */
	private TextView priorityText;
	/** 标签 */
	private TextView tagsText;
	/** 情境（地点）（上下文） */
	private TextView contextText;
	/** 提醒 */
	private TextView alarmText;


	//=====第二页=====
	/** 当前状态 */
	private TextView stateText;
	/** 前驱任务 */
	private TextView previousTaskText;
	/** 后续任务 */
	private TextView followingTaskText;
	/** 已用时间 */
	private TextView usedTimeText;
	/** 预计总时间 */
	private TextView totalTimeText;
	/** 中断次数 */
	private TextView interruptText;

	/**
	 * @return 操作的那个task，做过的修改都存在里头了
	 */
	public TaskInfo getTask() {
		return this.task;
	}
	
	/**
	 * @return 任务详细信息部分，第一个page（即page0）的View，task基本信息的description
	 */
	public View getDescriptionView() {
		View view = LayoutInflater.from(context).inflate(R.layout.taskdetails_paged_view_item1, null);

		//=====任务名称、备注=====
		taskNameText = (EditText) view.findViewById(R.id.taskNameEditText);
		taskMemoText = (EditText) view.findViewById(R.id.taskMemoEditText);
		this.refreshTaskName();
		this.refreshTaskMemo();
		
		//=====任务时间相关信息：周期、开始、截止=====
		periodicInfoText = (TextView) view.findViewById(R.id.taskPeriodicInfoTextView);
		startTimeText = (TextView) view.findViewById(R.id.taskStartTimeTextView);
		deadlineText = (TextView) view.findViewById(R.id.taskDeadlineTextView);
		this.refreshPeriodicInfo();
		this.refreshStartTime();
		this.refreshDeadline();
		this.initTaskTimePartsUI();

		//=====任务逻辑相关信息：优先级、标签、情境地点、提醒=====
		priorityText = (TextView) view.findViewById(R.id.taskPriorityTextView);
		tagsText = (TextView) view.findViewById(R.id.taskTagsTextView);
		contextText = (TextView) view.findViewById(R.id.taskContextTextView);
		alarmText = (TextView) view.findViewById(R.id.taskAlarmTextView);
		this.refreshPriority();
		this.refreshTags();
		this.refreshContext();
		this.refreshAlarm();
		this.initTaskLogicPartsUI();
		
		return view;
	}
	
	/**
	 * 初始化与task的时间相关的UI部分：周期信息、开始时间、截止时间
	 */
	private void initTaskTimePartsUI() {
		//=====周期信息=====
		periodicInfoText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO ①无重复；②每隔多少天；③每周周几
			}
		});

		//=====开始时间=====
		startTimeText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSetTimeDialog(true);
			}
		});
		
		//=====截止时间=====
		deadlineText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSetTimeDialog(false);
			}
		});
	}

	/**
	 * 显示设置时间的dialog
	 * @param isStartTime true则设置开始时间、false则为设置截止时间
	 */
	private void showSetTimeDialog(final boolean isStartTime) {
		//=====新建builder=====
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("设置" + context.getString(isStartTime ? R.string.Detail_starttime_intro_text
								: R.string.Detail_deadline_intro_text));
		View timeView = LayoutInflater.from(context).inflate(R.layout.dialog_set_time, null);
		builder.setView(timeView);
		
		//=====初始化dialog上的UI=====
		final DatePicker datePicker = (DatePicker) timeView.findViewById(R.id.dialog_set_time_datePicker);
		final TimePicker timePicker = (TimePicker) timeView.findViewById(R.id.dialog_set_time_timePicker);
		final CheckBox checkBox = (CheckBox) timeView.findViewById(R.id.dialog_set_time_checkBox);
		timePicker.setIs24HourView(true);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//=====选中->timepicker disabled=====
				timePicker.setEnabled(!isChecked);
			}
		});
		
		//=====恢复时间到dialog的UI上，如果有的话=====
		Date previousDate = isStartTime ? task.getStarttime() : task.getDeadline();
		if (previousDate != null) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(previousDate);
			datePicker.updateDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
			if (TimeUtil.isFullDay(calendar)) {
				checkBox.setChecked(true);
			}
			else {
				checkBox.setChecked(false);
				timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
				timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
			}
		}

		//=====确认按钮做什么=====
		builder.setPositiveButton(R.string.Dialog_confirm_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//=====读datepicker=====
				Calendar calendar = new GregorianCalendar();
				calendar.set(Calendar.YEAR, datePicker.getYear());
				calendar.set(Calendar.MONTH, datePicker.getMonth());
				calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());

				//=====读timepicker=====
				if (!checkBox.isChecked()) {
					calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
					calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
				}
				else {
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
				}

				//=====设置结果=====
				if (isStartTime) {
					task.setStarttime(calendar.getTime());
					refreshStartTime();
				}
				else {
					task.setDeadline(calendar.getTime());
					refreshDeadline();					
				}
			}
		});
		
		//=====取消按钮做什么=====
		builder.setNegativeButton(R.string.Dialog_cancel_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
	
	/**
	 * 初始化与task逻辑相关的UI部分：优先级、标签、地点、提醒
	 */
	private void initTaskLogicPartsUI() {
		//=====优先级=====
		priorityText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSetPriorityDialog();
			}
		});
		
		//=====标签=====
		tagsText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSetTagsDialog();
			}
		});
		
		//=====情境，地点=====
		contextText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
		
		//=====提醒=====
		alarmText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
	}
	
	/**
	 * 显示设置优先级的dialog
	 */
	private void showSetPriorityDialog() {
		//=====初始化设置builder=====
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("设置" + context.getString(R.string.Detail_priority_intro_text));
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_priority, null);
		builder.setView(view);
		final RadioButton highButton = (RadioButton) view.findViewById(R.id.dialog_set_priority_high);
		final RadioButton middleButton = (RadioButton) view.findViewById(R.id.dialog_set_priority_middle);
		final RadioButton lowButton = (RadioButton) view.findViewById(R.id.dialog_set_priority_low);
		final RadioButton noneButton = (RadioButton) view.findViewById(R.id.dialog_set_priority_none);
		
		//=====根据已有参数先恢复UI=====
		switch (task.getPriority()) {	//TODO priority修改之后改这里
		case 0:
			highButton.setChecked(true);
			break;
		case 1:
			middleButton.setChecked(true);
			break;
		case 2:
			lowButton.setChecked(true);
			break;

		default:
			noneButton.setChecked(true);
			break;
		}
		
		//=====确认按钮做什么=====
		builder.setPositiveButton(R.string.Dialog_confirm_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO 当后台定了以后改这里
				if (highButton.isChecked()) {
					task.setPriority(0);
				}
				else if (middleButton.isChecked()) {
					task.setPriority(1);
				}
				else if (lowButton.isChecked()) {
					task.setPriority(2);
				}
				else if (noneButton.isChecked()) {
					task.setPriority(-1);
				}
				refreshPriority();
			}
		});
		builder.setNegativeButton(R.string.Dialog_cancel_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
	
	/**
	 * 显示设置tags的dialog
	 */
	private void showSetTagsDialog() {
		//TODO 可能要用到list adapter，比较复杂，后头再来写吧
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("设置" + context.getString(R.string.Detail_tag_intro_text));
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_tags, null);
		builder.setView(view);
		
		builder.setPositiveButton(R.string.Dialog_confirm_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		builder.setNegativeButton(R.string.Dialog_cancel_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
	
	/**
	 * 刷新TaskName的编辑框
	 */
	private void refreshTaskName() {
		taskNameText.setText(task.getName());
	}

	/**
	 * 刷新TaskMemo的编辑框
	 */
	private void refreshTaskMemo() {
		taskMemoText.setText(task.getHint());
	}
	
	/**
	 * 刷新周期信息
	 */
	private void refreshPeriodicInfo() {
		//TODO 周期信息如何显示
	}
	
	/**
	 * 刷新开始时间
	 */
	private void refreshStartTime() {
		Date startTime = task.getStarttime();
		if (startTime == null) {
			startTimeText.setTextColor(context.getResources().getColor(R.color.grey));
			startTimeText.setText(R.string.Detail_none);
		}
		else {
			if (new Date().getTime() >= startTime.getTime()) {
				//=====超期了，用红色=====
				startTimeText.setTextColor(context.getResources().getColor(R.color.red));				
			}
			else {
				//=====否则用蓝色=====
				startTimeText.setTextColor(context.getResources().getColor(R.color.blue));
			}
			startTimeText.setText(TimeUtil.isFullDay(startTime) ? TimeUtil
					.parseDate(startTime) : TimeUtil.parseDateTime(startTime));
		}
	}
	
	/**
	 * 刷新截止时间
	 */
	private void refreshDeadline() {
		Date deadline = task.getDeadline();
		if (deadline == null) {
			deadlineText.setTextColor(context.getResources().getColor(R.color.grey));
			deadlineText.setText(R.string.Detail_none);
		}
		else {
			if (new Date().getTime() >= deadline.getTime()) {
				//=====超期了，用红色=====
				deadlineText.setTextColor(context.getResources().getColor(R.color.red));
			}
			else {
				//=====否则用蓝色=====
				deadlineText.setTextColor(context.getResources().getColor(R.color.blue));
			}
			deadlineText.setText(TimeUtil.isFullDay(deadline) ? TimeUtil
					.parseDate(deadline) : TimeUtil.parseDateTime(deadline));
		}
	}

	/**
	 * 刷新优先级
	 */
	private void refreshPriority() {
		//TODO 等后台的priority的类型定好（enum || static final int）
		switch (task.getPriority()) {
		case 0:
			//HIGH
			priorityText.setTextColor(context.getResources().getColor(R.color.high_priority));
			priorityText.setText(R.string.Detail_high_priority_text);
			break;
		case 1:
			//MIDDLE
			priorityText.setTextColor(context.getResources().getColor(R.color.mid_priority));
			priorityText.setText(R.string.Detail_middle_priority_text);
			break;
		case 2:
			//LOW
			priorityText.setTextColor(context.getResources().getColor(R.color.low_priority));
			priorityText.setText(R.string.Detail_low_priority_text);
			break;

		default:
			priorityText.setTextColor(context.getResources().getColor(R.color.grey));
			priorityText.setText(R.string.Detail_unset);
			break;
		}
	}
	
	/**
	 * 刷新标签
	 */
	private void refreshTags() {
		ArrayList<String> tags = task.ExportTag();
		if (tags == null || tags.isEmpty()) {
			tagsText.setTextColor(context.getResources().getColor(R.color.grey));
			tagsText.setText(R.string.Detail_none);
		}
		else {
			tagsText.setTextColor(context.getResources().getColor(R.color.black));			
			String message = tags.get(0);
			for (int i = 1; i < tags.size(); i++) {
				message += (", " + tags.get(i));
			}
			tagsText.setText(message);
		}
	}
	
	/**
	 * 刷新情境（上下文、地点）
	 */
	private void refreshContext() {
		String address = task.getAddr();
		if (address == null || address.isEmpty()) {
			contextText.setTextColor(context.getResources().getColor(R.color.grey));
			contextText.setText(R.string.Detail_none);
		}
		else {
			contextText.setTextColor(context.getResources().getColor(R.color.blue));
			contextText.setText(address);
		}
	}
	
	/**
	 *  刷新提醒
	 */
	private void refreshAlarm() {
		//TODO
	}
	
	
	
	/**
	 * @return 任务详细信息界面，第二个page（即page1）的View，task任务进度、完成信息的展示
	 */
	public View getProgressView() {
		View view = LayoutInflater.from(context).inflate(R.layout.taskdetails_paged_view_item2, null);
		
		//=====任务当前状态=====
		stateText = (TextView) view.findViewById(R.id.taskStateTextView);
		this.refreshState();
		this.initTaskStatePartsUI();
		
		//=====任务先后顺序=====
		previousTaskText = (TextView) view.findViewById(R.id.taskPreviousTextView);
		followingTaskText = (TextView) view.findViewById(R.id.taskFollowingTextView);
		this.refreshPrevious();
		this.refreshFollowing();
		this.initTaskSequencePartsUI();

		//=====任务执行情况=====
		usedTimeText = (TextView) view.findViewById(R.id.taskUsedTimeTextView);
		totalTimeText = (TextView) view.findViewById(R.id.taskTotalTimeTextView);
		interruptText = (TextView) view.findViewById(R.id.taskInterruptTextView);
		this.refreshUsedTime();
		this.refreshTotalTime();
		this.refreshInterrupt();

		return view;
	}

	/**
	 *  初始化任务状态部分的UI
	 */
	private void initTaskStatePartsUI() {
		stateText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
	}
	
	/**
	 * 初始化任务先后顺序部分的UI
	 */
	private void initTaskSequencePartsUI() {
		//=====前驱任务=====
		previousTaskText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (previousTask == null || !(context instanceof Activity)) {
					return;
				}
				
				Bundle bundle = new Bundle();
				bundle.putLong(OverviewTaskListActivity.TASK_ID_STR, previousTask.getId());
				ActivityUtil.startActivityWithBundle((Activity)context, TaskDetailsActivity.class, 0, false, bundle);					
			}
		});
		
		//=====后续任务=====
		followingTaskText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (followingTask == null || !(context instanceof Activity)) {
					return;
				}
				
				Bundle bundle = new Bundle();
				bundle.putLong(OverviewTaskListActivity.TASK_ID_STR, followingTask.getId());
				ActivityUtil.startActivityWithBundle((Activity)context, TaskDetailsActivity.class, 0, false, bundle);
			}
		});
	}

	/**
	 * 刷新任务状态
	 */
	private void refreshState() {
		//TODO
	}
	
	/**
	 * 刷新前驱任务的显示
	 */
	private void refreshPrevious() {
		if (this.previousTask == null) {
			previousTaskText.setTextColor(context.getResources().getColor(R.color.grey));
			previousTaskText.setText(R.string.Detail_none);
		}
		else {
			previousTaskText.setTextColor(context.getResources().getColor(R.color.blue));
			previousTaskText.setText(previousTask.getName());
		}
	}
	
	/**
	 * 刷新后续任务的显示
	 */
	private void refreshFollowing() {
		if (this.followingTask == null) {
			followingTaskText.setTextColor(context.getResources().getColor(R.color.grey));
			followingTaskText.setText(R.string.Detail_none);
		}
		else {
			followingTaskText.setTextColor(context.getResources().getColor(R.color.blue));
			followingTaskText.setText(followingTask.getName());
		}
	}
	
	/**
	 * 刷新已用时间
	 */
	private void refreshUsedTime() {
		int minutes = task.getFinishedCycle();
		if (minutes == -1) {
			usedTimeText.setTextColor(context.getResources().getColor(R.color.grey));
			usedTimeText.setText(R.string.Detail_none);
		}
		else {
			usedTimeText.setTextColor(context.getResources().getColor(R.color.blue));
			int pomoDuration = new Settings(context).getPomotimerDuration();
			if (minutes % pomoDuration == 0) {
				//=====整数个番茄周期=====
				usedTimeText.setText(minutes / pomoDuration + "个番茄周期");
			}
			else {
				//=====非整数个番茄周期=====
				int cycle = minutes / pomoDuration;
				usedTimeText.setText(cycle + "~" + (cycle+1) + "个番茄周期");
			}
		}
	}
	
	/**
	 * 刷新预计总时间
	 */
	private void refreshTotalTime() {
		//TODO 等变量更新，改这里
		int minutes = task.getUnfinishedCycle();
		if (minutes == -1) {
			totalTimeText.setTextColor(context.getResources().getColor(R.color.grey));
			totalTimeText.setText(R.string.Detail_unset);
		}
		else {
			totalTimeText.setTextColor(context.getResources().getColor(R.color.blue));
			int pomoDuration = new Settings(context).getPomotimerDuration();
			if (minutes % pomoDuration == 0) {
				//=====整数个番茄周期=====
				totalTimeText.setText(minutes / pomoDuration + "个番茄周期");
			}
			else {
				//=====非整数个番茄周期=====
				int cycle = minutes / pomoDuration;
				totalTimeText.setText(cycle + "~" + (cycle+1) + "个番茄周期");
			}
		}
	}
	
	/**
	 * 刷新中断次数显示
	 */
	private void refreshInterrupt() {
		/*
		 * 不能直接传入一个int，那会被认为是resource_id，会崩
		 */
		this.interruptText.setText(task.getInterrupt() + "");
	}
}
