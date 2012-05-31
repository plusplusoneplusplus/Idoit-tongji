package edu.tongji.fiveidiots.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ctrl.TaskInfo;
import edu.tongji.fiveidiots.util.TestingHelper;

/**
 * 主要负责管理tasks的显示和业务逻辑控制
 * @author Andriy @author IRainbow5
 */
public class OverviewTaskListActivity extends OverviewTagListActivity{

	private ListView taskListView;
	private TaskSheetType currentTaskSheetType = TaskSheetType.TODAY;
	private List<TaskInfo> tasks = new ArrayList<TaskInfo>();
	private TaskListAdapter adapter = new TaskListAdapter();
	
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
		switch (currentTaskSheetType) {
		case POOL:
			//TODO 得到所有收集池里的任务
			tasks = new ArrayList<TaskInfo>();
			break;

		case TODAY:
			//TODO 得到所有今日任务
			tasks = TestingHelper.getRandomTaskList();
			break;

		case FUTURE:
			//TODO 得到所有未来任务
			tasks = new ArrayList<TaskInfo>();
			break;

		case PERIODIC:
			//TODO 得到所有周期性任务
			tasks = new ArrayList<TaskInfo>();
			break;

		case ALL:
			//TODO 得到所有所有任务
			tasks = new ArrayList<TaskInfo>();
			break;

		default:
			tasks = new ArrayList<TaskInfo>();
			break;
		}

		this.taskListView.setAdapter(this.adapter);
		this.taskListView.setOnItemClickListener(this.adapter);
		this.registerForContextMenu(this.taskListView);
	}
	
	/**
	 * 用于listview的创建context menu
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.task_list_long_click_menus, menu);
		menu.setHeaderTitle(R.string.TL_longclicked_operations);
	}

	/**
	 * 用于处理listview中的相应，可以得到长按住的那个position
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.TL_longclicked_edit:
			//TODO
			Toast.makeText(this, "pos: " + info.position, Toast.LENGTH_SHORT).show();
			return true;
		case R.id.TL_longclicked_delete:
			//TODO
			Toast.makeText(this, "pos: " + info.position, Toast.LENGTH_SHORT).show();
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * 用来显示task listview的adapter
	 * @author Andriy
	 */
	private class TaskListAdapter extends BaseAdapter implements OnItemClickListener{

		// ===为什么选-2呢，因为后头有一个用（selectedPos+1）来比较，如果是-1，则[0]会中枪===
		private final int NOT_SELECTED = -2;
		/**
		 * 当前选中的item的position，如果==NOT_SELECTED，表示没有选中
		 */
		private int selectedPos = NOT_SELECTED;
		
		@Override
		public int getCount() {
			if (selectedPos == NOT_SELECTED) {
				return tasks.size();
			}
			else {
				return tasks.size() + 1;
			}
		}

		@Override
		public Object getItem(int position) {
			//=====根据listview中的位置获得data list中的位置=====
			if (selectedPos == NOT_SELECTED) {
				return tasks.get(position);
			}
			else {
				if (position <= selectedPos) {
					return tasks.get(position);
				}
				else if (position == selectedPos+1) {
					return tasks.get(selectedPos);
				}
				else {
					return tasks.get(position-1);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			//=====根据数据list中的位置获得listview中的位置=====
			if (selectedPos == NOT_SELECTED) {
				return position;
			}
			else {
				if (position <= selectedPos) {
					return position;
				}
				else {
					return position+1;
				}
			}
		}

		/**
		 * 刷一个brief information的界面
		 * @param task 用此task的信息
		 * @return
		 */
		private View getBriefView(TaskInfo task) {
			View view = LayoutInflater.from(OverviewTaskListActivity.this).inflate(R.layout.tasklist_item_brief, null);
			switch (task.getPri()) {
			case 0:
				view.setBackgroundResource(R.drawable.high_priority_bg);
				break;
			case 1:
				view.setBackgroundResource(R.drawable.mid_priority_bg);
				break;
			case 2:
				view.setBackgroundResource(R.drawable.low_priority_bg);
				break;
			default:
				break;
			}

			TextView taskNameTextView = (TextView) view.findViewById(R.id.TL_taskNameTextView);
			TextView startTimeTextView = (TextView) view.findViewById(R.id.TL_startTimeTextView);
			TextView leftTimeTextView = (TextView) view.findViewById(R.id.TL_leftTimeTextView);

			taskNameTextView.setText(task.getName());
			startTimeTextView.setText(task.getStarttime() + "");
			leftTimeTextView.setText(task.getDeadline() + "");				

			return view;
		}
		
		/**
		 *  刷一个extended information的界面
		 * @param task 用此task的信息
		 * @return
		 */
		private View getExtendedView(final TaskInfo task) {
			View view = LayoutInflater.from(OverviewTaskListActivity.this).inflate(R.layout.tasklist_item_extended, null);

			TextView memoTextView = (TextView) view.findViewById(R.id.TL_memoTextView);
			TextView progressTextView = (TextView) view.findViewById(R.id.TL_progressTextView);
			Button startButton = (Button) view.findViewById(R.id.TL_startButton);
			CheckBox finishBox = (CheckBox) view.findViewById(R.id.TL_finishCheckBox);
			
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
			
			return view;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView != null) {
				//据说这里要判断、要优化，不知道怎么确认老的view和新的view是同一种类型并且不需要重绘
			}
			
			if (selectedPos == NOT_SELECTED) {
				//=====说明没有选中，每一个都是brief information=====
				TaskInfo task = tasks.get(position);
				convertView = this.getBriefView(task);
			}
			else {
				if (position <= selectedPos) {
					//=====要展示的是selected的之前的部分，直接展示brief即可=====
					TaskInfo task = tasks.get(position);
					convertView = this.getBriefView(task);
				}
				else if (position == selectedPos+1) {
					//=====就是你了！是extended information=====
					TaskInfo task = tasks.get(selectedPos);
					convertView = this.getExtendedView(task);
				}
				else {
					//=====详细的部分已经过了，直接取x-1的那个task展示brief即可=====
					TaskInfo task = tasks.get(position-1);
					convertView = this.getBriefView(task);
				}
			}
			return convertView;
		}

		/**
		 * 此TaskListAdapter不仅仅管理data source，还处理item click！碉堡了！
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (selectedPos == NOT_SELECTED) {
				selectedPos = position;
			}
			else {
				if (position < selectedPos) {
					selectedPos = position;
				}
				else if (position == selectedPos) {
					//=====之前选中，现在就不选中=====
					selectedPos = NOT_SELECTED;
				}
				else if (position == selectedPos+1) {
					//=====其实不用处理extended情况，因为它根本就不会被click到，不知道为什么=_======
					return;
				}
				else {	//position > selectedPos+1
					//=====需要减1的！=====
					selectedPos = position - 1;
				}
			}
			
			//=====通知数据更新了，该刷新界面了=====
			this.notifyDataSetChanged();
		}
	}

}
