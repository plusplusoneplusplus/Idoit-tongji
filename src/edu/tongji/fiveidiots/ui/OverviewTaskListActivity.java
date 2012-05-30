package edu.tongji.fiveidiots.ui;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ctrl.TaskInfo;
import edu.tongji.fiveidiots.util.TestingHelper;

/**
 * 主要负责管理tasks的显示和业务逻辑控制
 * @author Andriy @author IRainbow5
 */
public class OverviewTaskListActivity extends OverviewActionBarActivity{

	private ListView taskListView;
	private TaskSheetType currentTaskSheetType = TaskSheetType.TODAY;
	
	private static enum TaskSheetType {
		TODAY,		//今日
		FUTURE,		//已经确定好开始时间（非今天）
		PERIODIC,	//周期性任务
		POOL,			//收集池
		ALL				//全部
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        taskListView = (ListView) findViewById(R.id.taskListView);
        
        Button testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//=====用于测试=====
				resetTaskList();
			}
		});
	}

	/**
	 * 因为此activity终将继承于GDActivity，告诉其加载哪个layout
	 */
	@Override
	protected void mySetActionBarContentView() {
		setActionBarContentView(R.layout.taskoverview);
	}

	/**
	 * 刷新任务list的listview
	 */
	private void resetTaskList() {
		List<TaskInfo> tasks;
		switch (currentTaskSheetType) {
		case POOL:
			//TODO 得到所有收集池里的任务
			tasks = null;
			break;

		case TODAY:
			//TODO 得到所有今日任务
			tasks = TestingHelper.getRandomTaskList();
			break;

		case FUTURE:
			//TODO 得到所有未来任务
			tasks = null;
			break;

		case PERIODIC:
			//TODO 得到所有周期性任务
			tasks = null;
			break;

		case ALL:
			//TODO 得到所有所有任务
			tasks = null;
			break;

		default:
			tasks = null;
			break;
		}

		this.taskListView.setAdapter(new TaskListAdapter(tasks));
	}
	
	/**
	 * 用来显示task listview的adapter
	 * @author Andriy
	 */
	private class TaskListAdapter extends BaseAdapter {

		private final List<TaskInfo> tasks;
		public TaskListAdapter(List<TaskInfo> aTaskInfos) {
			this.tasks = aTaskInfos;
		}
		
		@Override
		public int getCount() {
			return this.tasks.size() * 2;
		}

		@Override
		public Object getItem(int position) {
			int index = position >> 1;
			return this.tasks.get(index);
		}

		@Override
		public long getItemId(int position) {
			return position * 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final TaskInfo task = (TaskInfo) this.getItem(position);
			if (convertView != null) {
				//据说这里要判断、要优化，不知道怎么确认老的view和新的view是同一种类型并且不需要重绘
			}

			if (position % 2 == 0) {
				//=====偶数，说明是brief information=====
				convertView = LayoutInflater.from(OverviewTaskListActivity.this).inflate(R.layout.tasklist_item_brief, null);
				convertView.setBackgroundColor(OverviewTaskListActivity.this.getResources().getColor(R.color.low_priority));

				TextView taskNameTextView = (TextView) convertView.findViewById(R.id.TL_taskNameTextView);
				TextView startTimeTextView = (TextView) convertView.findViewById(R.id.TL_startTimeTextView);
				TextView leftTimeTextView = (TextView) convertView.findViewById(R.id.TL_leftTimeTextView);

//				taskNameTextView.setTextColor();
				taskNameTextView.setText(task.getName());
				startTimeTextView.setText(task.getStarttime() + "");
				leftTimeTextView.setText(task.getDeadline() + "");
			}
			else {
				//=====奇数，说明是extended information=====
				convertView = LayoutInflater.from(OverviewTaskListActivity.this).inflate(R.layout.tasklist_item_extended, null);
//				convertView.setVisibility(View.INVISIBLE);				
				TextView memoTextView = (TextView) convertView.findViewById(R.id.TL_memoTextView);
				TextView progressTextView = (TextView) convertView.findViewById(R.id.TL_progressTextView);
				Button startButton = (Button) convertView.findViewById(R.id.TL_startButton);
				CheckBox finishBox = (CheckBox) convertView.findViewById(R.id.TL_finishCheckBox);
				
				memoTextView.setText(task.getHint());
				progressTextView.setText("past" + " / " + "total");
				startButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Toast.makeText(OverviewTaskListActivity.this, "task: " + task.getName()
										+ " is about to start", Toast.LENGTH_SHORT).show();
					}
				});
				finishBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						Toast.makeText(OverviewTaskListActivity.this, "task: " + task.getName()
								+ " finished? " + isChecked, Toast.LENGTH_SHORT).show();
					}
				});
			}
			return convertView;
		}
		
	}
}
