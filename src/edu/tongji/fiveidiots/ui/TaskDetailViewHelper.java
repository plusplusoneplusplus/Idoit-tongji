package edu.tongji.fiveidiots.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ctrl.PeriodInfo;
import edu.tongji.fiveidiots.ctrl.TaskInfo;
import edu.tongji.fiveidiots.util.Settings;
import edu.tongji.fiveidiots.util.TestingHelper;
import edu.tongji.fiveidiots.util.TimeUtil;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

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
	private TaskInfo previousTask = null;
	/**
	 * 此任务的后续任务
	 */
	private TaskInfo followingTask = null;

	private final Context context;
	public TaskDetailViewHelper(Context aContext, TaskInfo aTask) {
		this.context = aContext;
		this.task = aTask;
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
	 * 操作的那个task，做过的修改都存在里头了
	 * @return 如果修改name为“”，会返回null
	 */
	public TaskInfo getTask() {
		this.task.setName(taskNameText.getText().toString());
		this.task.setHint(taskMemoText.getText().toString());
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
		/*
		 * name和memo的edittex无需额外操作，只需在总结task的时候toString检查一下就好了
		 */
		
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
				showSetPeriodicDialog();
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
	 * 显示设置重复任务信息的dialog
	 */
	private void showSetPeriodicDialog() {
		//=====生成、初始化builder=====
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("设置" + context.getString(R.string.Detail_period_intro_text));
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_periodic, null);
		builder.setView(view);

		//=====设置界面交互=====
		final RadioButton byDayButton = (RadioButton) view.findViewById(R.id.dialog_set_periodic_radioLeft);
		final RadioButton byWeekButton = (RadioButton) view.findViewById(R.id.dialog_set_periodic_radioRight);
		final EditText intervalEditText = (EditText) view.findViewById(R.id.dialog_set_periodic_editText);
		final ToggleButton monday = (ToggleButton) view.findViewById(R.id.dialog_set_periodic_mondayButton);
		final ToggleButton tuesday = (ToggleButton) view.findViewById(R.id.dialog_set_periodic_tuesdayButton);
		final ToggleButton wednesday = (ToggleButton) view.findViewById(R.id.dialog_set_periodic_wednesdayButton);
		final ToggleButton thursday = (ToggleButton) view.findViewById(R.id.dialog_set_periodic_thursdayButton);
		final ToggleButton friday = (ToggleButton) view.findViewById(R.id.dialog_set_periodic_fridayButton);
		final ToggleButton saturday = (ToggleButton) view.findViewById(R.id.dialog_set_periodic_saturdayButton);
		final ToggleButton sunday = (ToggleButton) view.findViewById(R.id.dialog_set_periodic_sundayButton);
		byDayButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				intervalEditText.setEnabled(isChecked);
				monday.setEnabled(!isChecked);
				tuesday.setEnabled(!isChecked);
				wednesday.setEnabled(!isChecked);
				thursday.setEnabled(!isChecked);
				friday.setEnabled(!isChecked);
				saturday.setEnabled(!isChecked);
				sunday.setEnabled(!isChecked);
			}
		});
		byWeekButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				intervalEditText.setEnabled(!isChecked);
				monday.setEnabled(isChecked);
				tuesday.setEnabled(isChecked);
				wednesday.setEnabled(isChecked);
				thursday.setEnabled(isChecked);
				friday.setEnabled(isChecked);
				saturday.setEnabled(isChecked);
				sunday.setEnabled(isChecked);
			}
		});
		
		//=====根据数据先恢复现场=====
		final PeriodInfo info = task.getPeriodInfo();
		switch (info.getPeriodType()) {
		case PeriodInfo.PERIOD_NONE:
			//do nothing
			break;
		case PeriodInfo.PERIOD_BY_DAY:
			byDayButton.setChecked(true);
			intervalEditText.setText(info.getIntervalByDay() + "");
			break;
		case PeriodInfo.PERIOD_BY_WEEK:
			byWeekButton.setChecked(true);
			Map<Integer, Boolean> map = info.getCheckedMapByWeek();
			monday.setChecked(map.get(1));
			tuesday.setChecked(map.get(2));
			wednesday.setChecked(map.get(3));
			thursday.setChecked(map.get(4));
			friday.setChecked(map.get(5));
			saturday.setChecked(map.get(6));
			sunday.setChecked(map.get(7));
			break;

		default:
			break;
		}

		//=====确认按钮，设置周期信息=====
		builder.setPositiveButton(R.string.Dialog_confirm_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PeriodInfo info = new PeriodInfo();
				if (byDayButton.isChecked()) {
					//=====by day=====
					String numberString = intervalEditText.getText().toString();
					if (numberString == null || numberString.isEmpty()) {
						return;
					}
					Integer interval = Integer.parseInt(numberString);
					if (interval <= 0) {
						Toast.makeText(context, "小于等于0的周期是没有意义的！", Toast.LENGTH_SHORT).show();
						return;
					}
					info.setPeriodByDay(interval);
				}
				else {
					//=====by week=====
					HashMap<Integer, Boolean> hashMap = new HashMap<Integer, Boolean>();
					hashMap.put(1, monday.isChecked());
					hashMap.put(2, tuesday.isChecked());
					hashMap.put(3, wednesday.isChecked());
					hashMap.put(4, thursday.isChecked());
					hashMap.put(5, friday.isChecked());
					hashMap.put(6, saturday.isChecked());
					hashMap.put(7, sunday.isChecked());
					if (!hashMap.containsValue(true)) {
						//=====没有设置一个有效的，quit=====
						return;
					}

					info.setPeriodByWeek(hashMap);
				}
				task.setPeriodInfo(info);
				refreshPeriodicInfo();
			}
		});
		
		//=====清除周期信息=====
		builder.setNeutralButton(R.string.Dialog_neutral_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PeriodInfo info = new PeriodInfo();
				info.setPeriodNone();
				task.setPeriodInfo(info);
				refreshPeriodicInfo();
			}
		});
		
		//=====取消，离开=====
		builder.setNegativeButton(R.string.Dialog_cancel_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
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
		Date previousDate = isStartTime ? task.getStartTime() : task.getDeadline();
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
					task.setStartTime(calendar.getTime());
					refreshStartTime();
				}
				else {
					task.setDeadline(calendar.getTime());
					refreshDeadline();					
				}
			}
		});
		
		//=====利用neutral button做什么=====
		builder.setNeutralButton(R.string.Dialog_neutral_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (isStartTime) {
					task.setStartTime(null);
					refreshStartTime();
				}
				else {
					task.setDeadline(null);
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
				showSetContextDialog();
			}
		});
		
		//=====提醒=====
		alarmText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (task.getStartTime() == null) {
					Toast.makeText(context, "先设置开始时间才能设置提醒", Toast.LENGTH_SHORT).show();
					return;
				}
				showSetAlarmDialog();
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
		switch (task.getPriority()) {
		case TaskInfo.PRIORITY_HIGH:
			highButton.setChecked(true);
			break;
		case TaskInfo.PRIORITY_MIDDLE:
			middleButton.setChecked(true);
			break;
		case TaskInfo.PRIORITY_LOW:
			lowButton.setChecked(true);
			break;
		case TaskInfo.PRIORITY_UNSET:
			noneButton.setChecked(true);
			break;

		default:
			break;
		}
		
		//=====确认按钮做什么=====
		builder.setPositiveButton(R.string.Dialog_confirm_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (highButton.isChecked()) {
					task.setPriority(TaskInfo.PRIORITY_HIGH);
				}
				else if (middleButton.isChecked()) {
					task.setPriority(TaskInfo.PRIORITY_MIDDLE);
				}
				else if (lowButton.isChecked()) {
					task.setPriority(TaskInfo.PRIORITY_LOW);
				}
				else if (noneButton.isChecked()) {
					task.setPriority(TaskInfo.PRIORITY_UNSET);
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
		//=====builder的生成与初始化=====
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("设置" + context.getString(R.string.Detail_tag_intro_text));
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_tags, null);
		builder.setView(view);
		final EditText editText = (EditText) view.findViewById(R.id.dialog_set_tags_editText);
		final Button addButton = (Button) view.findViewById(R.id.dialog_set_tags_addButton);
		final ListView listView = (ListView) view.findViewById(R.id.dialog_set_tags_listView);

		//=====设置adapter及交互操作=====
		final SetTagsAdapter tagsAdapter = new SetTagsAdapter();
		listView.setAdapter(tagsAdapter);
		addButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String text = editText.getText().toString();
				if (text == null || text.isEmpty()) {
					return;
				}
				//=====以空格分隔，每一个token尝试加入tag=====
				StringTokenizer tokenizer = new StringTokenizer(text, " ");
				while (tokenizer.hasMoreTokens()) {
					String s = tokenizer.nextToken();
					if (!task.ExportTags().contains(s)) {
						task.addTag(s);
					}
				}
				editText.setText("");
				tagsAdapter.notifyDataSetChanged();
			}
		});

		//=====因为没有确认和取消按键，按dialog之外或者按back就相当于cancel=====
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				refreshTags();
			}
		});
		builder.create().show();
	}
	
	/**
	 * 用于设置tags的dialog中的listview
	 * @author Andriy
	 */
	private class SetTagsAdapter extends BaseAdapter {
		private ArrayList<String> tags = task.ExportTags();

		@Override
		public int getCount() {
			return this.tags.size();
		}

		@Override
		public Object getItem(int position) {
			return this.tags.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(context).inflate(R.layout.dialog_set_tag_item, null);
			TextView textView = (TextView) convertView.findViewById(R.id.dialog_set_tag_item_textView);
			Button button = (Button) convertView.findViewById(R.id.dialog_set_tag_item_button);
			
			textView.setText(tags.get(position));
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					tags.remove(position);
					notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}
	
	/**
	 * 显示设置情境的dialog
	 */
	private void showSetContextDialog() {
		//=====初始化builder=====
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("设置" + context.getString(R.string.Detail_context_intro_text));
		final EditText editText = new EditText(context);
		builder.setView(editText);

		//=====如果有数据的话，恢复上去=====
		if (task.getAddr() != null) {
			editText.setText(task.getAddr());
		}
		
		//=====确认按钮做什么=====
		builder.setPositiveButton(R.string.Dialog_confirm_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				task.setAddr(editText.getText().toString());
				refreshContext();
			}
		});

		//=====中间按钮，清除=====
		builder.setNeutralButton(R.string.Dialog_neutral_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				task.setAddr(null);
				refreshContext();
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
	 * 显示设置提醒的dialog，只有当设置了开始时间才会弹出这个dialog
	 */
	private void showSetAlarmDialog() {
		//=====初始化builder，dialog的界面=====
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("设置" + context.getString(R.string.Detail_alarm_intro_text));
		final EditText editText = new EditText(context);
		editText.setHint("提前多少分钟");
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(editText);

		//=====确认按钮做什么=====
		builder.setPositiveButton(R.string.Dialog_confirm_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String numberString = editText.getText().toString();
				if (numberString == null || numberString.isEmpty()) {
					return;
				}

				//=====向前推number分钟=====
				Integer number = Integer.parseInt(numberString);
				task.setAlarm(new Date(task.getStartTime().getTime() - number * 60 * 1000));
				refreshAlarm();
			}
		});
		
		//=====利用neutral button做什么，清除！=====
		builder.setNeutralButton(R.string.Dialog_neutral_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				task.setAlarm(null);
				refreshAlarm();
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
		PeriodInfo info = task.getPeriodInfo();
		switch (info.getPeriodType()) {
		case PeriodInfo.PERIOD_NONE:
			periodicInfoText.setTextColor(context.getResources().getColor(R.color.grey));
			periodicInfoText.setText(context.getString(R.string.Detail_unset));
			break;

		case PeriodInfo.PERIOD_BY_DAY:
			periodicInfoText.setTextColor(context.getResources().getColor(R.color.blue));
			periodicInfoText.setText("每" + info.getIntervalByDay() + "天");
			break;

		case PeriodInfo.PERIOD_BY_WEEK:
			periodicInfoText.setTextColor(context.getResources().getColor(R.color.blue));
			Map<Integer, Boolean> map = info.getCheckedMapByWeek();
			ArrayList<String> list = new ArrayList<String>();
			if (map.get(1)) {
				list.add("一");
			}
			if (map.get(2)) {
				list.add("二");
			}
			if (map.get(3)) {
				list.add("三");
			}
			if (map.get(4)) {
				list.add("四");
			}
			if (map.get(5)) {
				list.add("五");
			}
			if (map.get(6)) {
				list.add("六");
			}
			if (map.get(7)) {
				list.add("日");
			}
			String message = "周" + list.get(0);
			for (int i = 1; i < list.size(); i++) {
				message += (", " + list.get(i));
			}
			periodicInfoText.setText(message);
			break;

		default:
			break;
		}
	}
	
	/**
	 * 刷新开始时间
	 */
	private void refreshStartTime() {
		Date startTime = task.getStartTime();
		if (startTime == null) {
			startTimeText.setTextColor(context.getResources().getColor(R.color.grey));
			startTimeText.setText(R.string.Detail_unset);
		}
		else {
			if (TimeUtil.isFullDay(startTime)) {
				Date tempDate = new Date();
				Date currentDate = new Date(tempDate.getYear(), tempDate.getMonth(), tempDate.getDate());
				if (currentDate.getTime() >= startTime.getTime()) {
					//=====全天任务超期了，用红色=====
					startTimeText.setTextColor(context.getResources().getColor(R.color.red));
				}
				else {
					//=====全天任务未超期，用蓝色=====
					startTimeText.setTextColor(context.getResources().getColor(R.color.blue));
				}
				startTimeText.setText(TimeUtil.parseDate(startTime));				
			}
			else {
				if (new Date().getTime() >= startTime.getTime()) {
					//=====非全天，也超期了，用红色=====
					startTimeText.setTextColor(context.getResources().getColor(R.color.red));				
				}
				else {
					//=====否则用蓝色=====
					startTimeText.setTextColor(context.getResources().getColor(R.color.blue));
				}
				startTimeText.setText(TimeUtil.parseDateTime(startTime));
			}
		}
	}
	
	/**
	 * 刷新截止时间
	 */
	private void refreshDeadline() {
		Date deadline = task.getDeadline();
		if (deadline == null) {
			deadlineText.setTextColor(context.getResources().getColor(R.color.grey));
			deadlineText.setText(R.string.Detail_unset);
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
		switch (task.getPriority()) {
		case TaskInfo.PRIORITY_HIGH:
			priorityText.setTextColor(context.getResources().getColor(R.color.high_priority));
			priorityText.setText(R.string.Detail_high_priority_text);
			break;

		case TaskInfo.PRIORITY_MIDDLE:
			priorityText.setTextColor(context.getResources().getColor(R.color.mid_priority));
			priorityText.setText(R.string.Detail_middle_priority_text);
			break;
		
		case TaskInfo.PRIORITY_LOW:
			priorityText.setTextColor(context.getResources().getColor(R.color.low_priority));
			priorityText.setText(R.string.Detail_low_priority_text);
			break;
		
		case TaskInfo.PRIORITY_UNSET:
			priorityText.setTextColor(context.getResources().getColor(R.color.grey));
			priorityText.setText(R.string.Detail_unset);
			break;

		default:
			break;
		}
	}
	
	/**
	 * 刷新标签
	 */
	private void refreshTags() {
		ArrayList<String> tags = task.ExportTags();
		if (tags == null || tags.isEmpty()) {
			tagsText.setTextColor(context.getResources().getColor(R.color.grey));
			tagsText.setText(R.string.Detail_unset);
		}
		else {
			tagsText.setTextColor(context.getResources().getColor(R.color.blue));			
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
			contextText.setText(R.string.Detail_unset);
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
		Date alarm = task.getAlarm();
		if (alarm == null) {
			alarmText.setTextColor(context.getResources().getColor(R.color.grey));
			alarmText.setText(context.getText(R.string.Detail_unset));
		}
		else {
			alarmText.setTextColor(context.getResources().getColor(R.color.blue));
			alarmText.setText(TimeUtil.parseDateTime(alarm));
		}
	}
	
	
	
	/**
	 * @return 任务详细信息界面，第二个page（即page1）的View，task任务进度、完成信息的展示
	 */
	public View getProgressView() {
		View view = LayoutInflater.from(context).inflate(R.layout.taskdetails_paged_view_item2, null);
		
		//=====任务当前状态=====
		stateText = (TextView) view.findViewById(R.id.taskStateTextView);
		this.refreshState();
		stateText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSetStateDialog();
			}
		});
		
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
		totalTimeText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSetTotalTimeDialog();
			}
		});

		return view;
	}
	
	/**
	 * 初始化任务先后顺序部分的UI
	 */
	private void initTaskSequencePartsUI() {
		//=====前驱任务=====
		previousTaskText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSetSequenceTaskDialog(true);
			}
		});
		
		//=====后续任务=====
		followingTaskText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSetSequenceTaskDialog(false);
			}
		});
	}
	
	/**
	 * 显示设置预期总时间的dialog
	 */
	private void showSetTotalTimeDialog() {
		//=====生成初始化builder及界面=====
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("设置" + context.getString(R.string.Detail_totaltime_intro_text));
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_total_time, null);
		builder.setView(view);
		final EditText hourText = (EditText) view.findViewById(R.id.dialog_set_total_time_hourEditText);
		final EditText minuteText = (EditText) view.findViewById(R.id.dialog_set_total_time_minuteEditText);
		
		//=====恢复现场=====
		if (task.getTotalTime() != -1) {
			int hour = task.getTotalTime() / 60;
			int minute = task.getTotalTime() % 60;
			hourText.setText(hour + "");
			minuteText.setText(minute + "");
		}

		//=====确认按钮做什么=====
		builder.setPositiveButton(R.string.Dialog_confirm_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String hourString = hourText.getText().toString();
				String minuteString = minuteText.getText().toString();
				if (hourString.isEmpty() && minuteString.isEmpty()) {
					//=====两个都是空的，就认为是清空吧=====
					task.setTotalTime(-1);
				}
				else {
					int minutes = 0;
					if (!hourString.isEmpty()) {
						Integer hour = Integer.parseInt(hourText.getText().toString());
						minutes += hour * 60;
					}
					if (!minuteString.isEmpty()) {
						Integer minute = Integer.parseInt(minuteText.getText().toString());
						minutes += minute;
					}
					task.setTotalTime(minutes);					
				}
				refreshTotalTime();
			}
		});
		
		//=====neutral button，清空=====
		builder.setNeutralButton(R.string.Dialog_neutral_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				task.setTotalTime(-1);
				refreshTotalTime();
			}
		});
		
		//=====取消按钮，直接退出=====
		builder.setNegativeButton(R.string.Dialog_cancel_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
	
	/**
	 * 显示设置前驱任务还是后续任务的dialog
	 * @param isPreviousTask true代表设置前驱任务，false代表后续任务
	 */
	private void showSetSequenceTaskDialog(final boolean isPreviousTask) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("设置" + context.getString(isPreviousTask ? R.string.Detail_previous_intro_text
								: R.string.Detail_following_intro_text));
		ListView listView = new ListView(context);
		builder.setView(listView);

		//TODO 调用真正数据库里的数据而不是random
		final SequenceTaskAdapter adapter = new SequenceTaskAdapter(TestingHelper.getRandomTaskList());
		listView.setAdapter(adapter);

		//=====neutral button，清除=====
		builder.setNeutralButton(R.string.Dialog_neutral_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (isPreviousTask) {
					task.setPrevTaskId(-1);
					previousTask = null;
					refreshPrevious();
				}
				else {
					task.setNextTaskId(-1);
					followingTask = null;
					refreshFollowing();
				}
			}
		});
		
		//=====取消，直接退出=====
		builder.setNegativeButton(R.string.Dialog_cancel_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		//=====选中某个item之后的动作=====
		final AlertDialog dialog = builder.create();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				TaskInfo taskInfo = adapter.getItem(position);
				if (isPreviousTask) {
					task.setPrevTaskId(taskInfo.getId());
					previousTask = taskInfo;
					refreshPrevious();
				}
				else {
					task.setNextTaskId(taskInfo.getId());
					followingTask = taskInfo;
					refreshFollowing();
				}
				dialog.cancel();
			}
		});
		dialog.show();
	}
	
	/**
	 * 用来在dialog中选取前驱或者后续任务时显示task的adpater
	 * @author Andriy
	 */
	private class SequenceTaskAdapter extends BaseAdapter {

		private List<TaskInfo> theTasks;
		public SequenceTaskAdapter(List<TaskInfo> tasks) {
			this.theTasks = tasks;
		}
		
		@Override
		public int getCount() {
			return theTasks.size();
		}

		@Override
		public TaskInfo getItem(int position) {
			return theTasks.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(context).inflate(R.layout.dialog_set_sequence_task_item, null);

			TextView textView = (TextView) convertView.findViewById(R.id.dialog_set_sequence_task_nameTextView);
			textView.setText(this.getItem(position).getName());
			
			return convertView;
		}
	}

	/**
	 * 显示设置任务状态的dialog
	 */
	private void showSetStateDialog() {
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("设置" + context.getString(R.string.Detail_state_intro_text));
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_state, null);
		builder.setView(view);
		
		final RadioButton normalButton = (RadioButton) view.findViewById(R.id.dialog_set_state_normalButton);
		final RadioButton finishedButton = (RadioButton) view.findViewById(R.id.dialog_set_state_finishedButton);
		final RadioButton deletedButton = (RadioButton) view.findViewById(R.id.dialog_set_state_deletedButton);
		
		//=====恢复现场=====
		switch (task.getStatus()) {
		case TaskInfo.STATUS_NORMAL:
			normalButton.setChecked(true);
			break;
		case TaskInfo.STATUS_FINISHED:
			finishedButton.setChecked(true);
			break;
		case TaskInfo.STATUS_DELETED:
			deletedButton.setChecked(true);
			break;

		default:
			break;
		}

		//=====确认按钮做啥子=====
		builder.setPositiveButton(R.string.Dialog_confirm_text, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (normalButton.isChecked()) {
					task.setStatus(TaskInfo.STATUS_NORMAL);
				}
				else if (finishedButton.isChecked()) {
					task.setStatus(TaskInfo.STATUS_FINISHED);
				}
				else if (deletedButton.isChecked()) {
					task.setStatus(TaskInfo.STATUS_DELETED);
				}
				refreshState();
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
	 * 刷新任务状态
	 */
	private void refreshState() {
		switch (task.getStatus()) {
		case TaskInfo.STATUS_NORMAL:
			stateText.setTextColor(context.getResources().getColor(R.color.blue));
			stateText.setText(R.string.Detail_state_normal);
			break;

		case TaskInfo.STATUS_FINISHED:
			stateText.setTextColor(context.getResources().getColor(R.color.green));
			stateText.setText(R.string.Detail_state_finished);
			break;

		case TaskInfo.STATUS_DELETED:
			stateText.setTextColor(context.getResources().getColor(R.color.black));
			stateText.setText(R.string.Detail_state_deleted);
			break;

		default:
			break;
		}
	}
	
	/**
	 * 刷新前驱任务的显示
	 */
	private void refreshPrevious() {
		if (this.previousTask == null) {
			previousTaskText.setTextColor(context.getResources().getColor(R.color.grey));
			previousTaskText.setText(R.string.Detail_unset);
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
			followingTaskText.setText(R.string.Detail_unset);
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
		int minutes = task.getUsedTime();
		if (minutes == 0) {
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
		int minutes = task.getTotalTime();
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
		interruptText.setTextColor(context.getResources().getColor(R.color.grey));
		this.interruptText.setText(task.getInterrupt() + "");
	}
}
