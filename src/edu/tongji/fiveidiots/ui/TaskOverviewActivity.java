package edu.tongji.fiveidiots.ui;

import java.util.List;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ctrl.TaskInfo;
import edu.tongji.fiveidiots.util.ActivityUtil;
import edu.tongji.fiveidiots.util.TestingHelper;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


/**
 * TaskOverviewActivity是总览任务的类
 */
public class TaskOverviewActivity extends GDActivity {
	
	public TaskOverviewActivity(){
		super(ActionBar.Type.Normal);
	}

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
        setActionBarContentView(R.layout.taskoverview);
        addActionBarItem(Type.Add);
        addActionBarItem(Type.Edit);

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

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		if (position == 0) {
			//第一个添加的东西，目前是add，当做进入计时器
			ActivityUtil.startNewActivity(this, PomotimerActivity.class, 0, false);
		}
		else {
			Toast.makeText(this, "bar_item at pos: " + position, Toast.LENGTH_SHORT).show();
		}
		return true;
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
			if (convertView != null) {
				//据说这里要判断、要优化，不知道怎么确认老的view和新的view是同一种类型并且不需要重绘
			}
			
			if (position % 2 == 0) {
				//=====偶数，说明是brief information=====
				convertView = LayoutInflater.from(TaskOverviewActivity.this).inflate(R.layout.tasklist_item_brief, null);
			}
			else {
				//=====奇数，说明是extended information=====
				convertView = LayoutInflater.from(TaskOverviewActivity.this).inflate(R.layout.tasklist_item_extended, null);
			}
			return convertView;
		}
		
	}
}
