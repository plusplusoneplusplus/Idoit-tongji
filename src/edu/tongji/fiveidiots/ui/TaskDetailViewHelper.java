package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ctrl.TaskInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

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
	private TextView interruptedText;

	
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
				//TODO
			}
		});

		//=====开始时间=====
		startTimeText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
		
		//=====截止时间=====
		deadlineText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
	}
	
	/**
	 * 初始化与task逻辑相关的UI部分：优先级、标签、地点、提醒
	 */
	private void initTaskLogicPartsUI() {
		//=====优先级=====
		priorityText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
		
		//=====标签=====
		tagsText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
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
		//TODO 判断time是全天还是具体，如果是全天则只显示年月日
		//还可以显示与现在隔多久
	}
	
	/**
	 * 刷新截止时间
	 */
	private void refreshDeadline() {
		//TODO 判断time是全天还是具体，如果是全天则只显示年月日
		//还可以显示与现在隔多久
	}

	/**
	 * 刷新优先级
	 */
	private void refreshPriority() {
		//TODO 等后台的priority的类型定好（enum || static final int）
		switch (task.getPri()) {
		case 0:
			priorityText.setText(R.string.Detail_high_priority_text);
			break;
		case 1:
			priorityText.setText(R.string.Detail_middle_priority_text);
			break;
		case 2:
			priorityText.setText(R.string.Detail_low_priority_text);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 刷新标签
	 */
	private void refreshTags() {
		//TODO
	}
	
	/**
	 * 刷新情境（上下文、地点）
	 */
	private void refreshContext() {
		//TODO
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
		interruptedText = (TextView) view.findViewById(R.id.taskInterruptTextView);
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
				//TODO
			}
		});
		
		//=====后续任务=====
		followingTaskText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
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
		//TODO
	}
	
	/**
	 * 刷新后续任务的显示
	 */
	private void refreshFollowing() {
		//TODO
	}
	
	/**
	 * 刷新已用时间
	 */
	private void refreshUsedTime() {
		//TODO
	}
	
	/**
	 * 刷新预计总时间
	 */
	private void refreshTotalTime() {
		//TODO
	}
	
	/**
	 * 刷新中断次数显示
	 */
	private void refreshInterrupt() {
		//TODO
	}
}
