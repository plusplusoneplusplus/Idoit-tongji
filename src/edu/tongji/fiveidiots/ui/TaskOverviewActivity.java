package edu.tongji.fiveidiots.ui;

import java.util.List;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ctrl.TaskInfo;
import edu.tongji.fiveidiots.util.ActivityUtil;
import edu.tongji.fiveidiots.util.TestingHelper;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBar.OnActionBarListener;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * TaskOverviewActivity是总览任务的类
 */
public class TaskOverviewActivity extends GDActivity {

	private QuickActionGrid mGridMore;
	private QuickActionGrid mGridTimeLine;
	
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
        setActionBarContentView(R.layout.idoit);
        
        prepareActionBar();
        prepareQuickActionGrid();
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
	
	private void prepareActionBar()
	{
		addActionBarItem(Type.Add, R.id.action_bar_add);
        addActionBarItem(Type.Eye, R.id.action_bar_timer);
        addActionBarItem(Type.List, R.id.action_bar_more);
	}
	
	private void prepareQuickActionGrid()
	{
		mGridMore = new QuickActionGrid(this);
		mGridMore.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_eye, R.string.exit));
		mGridMore.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_eye, R.string.exit));
		mGridMore.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_eye, R.string.exit));
		mGridMore.setOnQuickActionClickListener(mQuickActionListener);
	}
	
	private OnQuickActionClickListener mQuickActionListener = new OnQuickActionClickListener() {
		
		@Override
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
			Toast.makeText(TaskOverviewActivity.this, "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		
		//if press HOME actionbar item, the 'item' value is null, so catch it here
		if(position == OnActionBarListener.HOME_ITEM)
		{
			return true;
		}

		switch(item.getItemId())
		{
		case R.id.action_bar_add:
			
			break;
		case R.id.action_bar_timer:
			ActivityUtil.startNewActivity(this, PomotimerActivity.class, 0, false);
			break;
		case R.id.action_bar_more:
			onShowGrid(item.getItemView());
			break;
		default:
			break;	
		}
		return true;
	}
	
	public void onShowGrid(View v) {
        mGridMore.show(v);
    }
	
    private static class MyQuickAction extends QuickAction {
        
        private static final ColorFilter BLACK_CF = new LightingColorFilter(Color.BLACK, Color.BLACK);

        public MyQuickAction(Context ctx, int drawableId, int titleId) {
            super(ctx, buildDrawable(ctx, drawableId), titleId);
        }
        
        private static Drawable buildDrawable(Context ctx, int drawableId) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            d.setColorFilter(BLACK_CF);
            return d;
        }
        
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
				convertView = LayoutInflater.from(TaskOverviewActivity.this).inflate(R.layout.tasklist_item_brief, null);
				convertView.setBackgroundColor(TaskOverviewActivity.this.getResources().getColor(R.color.low_priority));

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
				convertView = LayoutInflater.from(TaskOverviewActivity.this).inflate(R.layout.tasklist_item_extended, null);
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
						Toast.makeText(TaskOverviewActivity.this, "task: " + task.getName()
										+ " is about to start", Toast.LENGTH_SHORT).show();
					}
				});
				finishBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						Toast.makeText(TaskOverviewActivity.this, "task: " + task.getName()
								+ " finished? " + isChecked, Toast.LENGTH_SHORT).show();
					}
				});
			}
			return convertView;
		}
		
	}

}
