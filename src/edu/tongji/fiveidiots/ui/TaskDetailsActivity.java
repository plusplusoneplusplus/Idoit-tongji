package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ctrl.TaskInfo;
import edu.tongji.fiveidiots.util.TestingHelper;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar.Type;
import greendroid.widget.ActionBarItem;
import greendroid.widget.PageIndicator;
import greendroid.widget.PagedAdapter;
import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 任务详细信息界面
 * 添加多个详细信息界面时修改PAGE_COUNT以及adapter中的getView方法
 * 在res/layout中添加对应界面布局文件
 * 其它不用修改
 * @author Andriy  @author IRainbow5
 */

public class TaskDetailsActivity extends GDActivity {
	
	/**
	 * 一共要有几页，一般2页就够了吧，必要+简要；复杂+
	 */
	private static final int PAGE_COUNT = 2;
	/**
	 * 去掉自己之后有多少，在next和previous的indicator中有用
	 */
	private static final int PAGE_EXCLUSIVE_COUNT= PAGE_COUNT - 1;
	
	//=====UI=====
	/**
	 * 右下角的page indicator，代表next
	 */
	private PageIndicator pageIndicatorNext;
	/**
	 * 左下角的page indicator，代表previous
	 */
	private PageIndicator pageIndicatorPrevious;
	/**
	 * 顶部的page indicator，代表本页在总的之中的index
	 */
	private PageIndicator pageIndicatorTop;
	private PagedView pagedView;
	
	/**
	 * 要展示details的对应task
	 */
	private TaskInfo task;
	
	/**
	 * actionbar为空
	 */
	public TaskDetailsActivity() {
		super(Type.Empty);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //=====从传入的Intent中拿到TaskID=====
		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null) {
			throw new IllegalStateException("没有传递一个带有TASK_ID的bundle怎么可以到TaskDetailActivity来！");
		}
		if (bundle != null) {
			long taskID = bundle.getLong(OverviewTaskListActivity.TASK_ID_STR, -1);
			//TODO 测试时直接随机生成一个task来用，到时候要新开一个线程异步取data，然后刷新
			task = TestingHelper.getRandomTask();
			task.setId(taskID);
		}

		//=====GDActivity，及搞定action bar上的item=====
        this.setActionBarContentView(R.layout.taskdetails_paged_view);
		this.addActionBarItem(ActionBarItem.Type.Compose, R.id.detail_action_bar_save);
        this.addActionBarItem(ActionBarItem.Type.Trashcan, R.id.detail_action_bar_delete);

        //=====找到pagedView=====
        pagedView = (PagedView) findViewById(R.id.paged_view);
        pagedView.setAdapter(new PhotoSwipeAdapter());
        pagedView.setOnPageChangeListener(pagedViewChangedListener);

        //=====找到，init page indicators=====
        pageIndicatorNext = (PageIndicator) findViewById(R.id.page_indicator_next);
        pageIndicatorPrevious = (PageIndicator) findViewById(R.id.page_indicator_prev);
        pageIndicatorTop = (PageIndicator) findViewById(R.id.page_indicator_top);
        this.initPageIndicators();
    }
    
    /**
     * init 3个page indicators: prev, next, top
     */
    private void initPageIndicators() {
    	//=====NEXT=====
        pageIndicatorNext.setDotCount(PAGE_EXCLUSIVE_COUNT);
        pageIndicatorNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
	            pagedView.smoothScrollToNext();
			}
        });

        //=====PREVIOUS=====
        pageIndicatorPrevious.setDotCount(PAGE_EXCLUSIVE_COUNT);
        pageIndicatorPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                pagedView.smoothScrollToPrevious();
			}
        });

        //=====TOP(GENERAL)=====
        pageIndicatorTop.setDotCount(PAGE_COUNT);
    }
    
    @Override
	protected void onResume() {
		super.onResume();

		//=====然后整个activity就是visible的了，因此这里刷UI=====
		this.setTitle("任务详细信息");
        this.setActivePage(pagedView.getCurrentPage());
	}

    @Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
    	switch (item.getItemId()) {
		case R.id.detail_action_bar_save:
			//TODO 保存当前做的修改
			Toast.makeText(this, "to be saved", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.detail_action_bar_delete:
			//TODO 删除当前task，返回前一个tasklist-overview，而且应该要求它自动刷新
			Toast.makeText(this, "to be deleted", Toast.LENGTH_SHORT).show();
			return true;

		default:
			return super.onHandleActionBarItemClick(item, position);
		}
	}

	/**
     * 设置当前在哪一个page
     * @param page
     */
	private void setActivePage(int page) {
        pageIndicatorTop.setActiveDot(page);
        pageIndicatorNext.setActiveDot(PAGE_EXCLUSIVE_COUNT- page);
        pageIndicatorPrevious.setActiveDot(page);
    }
    
    /**
     * PagedView如果左右滑动了，当前页面变化了，看这里
     */
	private OnPagedViewChangeListener pagedViewChangedListener = new OnPagedViewChangeListener() {

        @Override
        public void onStopTracking(PagedView pagedView) {
        }

        @Override
        public void onStartTracking(PagedView pagedView) {
        }

        @Override
        public void onPageChanged(PagedView pagedView, int previousPage, int newPage) {
            setActivePage(newPage);
        }
    };
    
    /**
     * 用于page平滑左右滑动的adapter
     * @author Andriy @author IRainbow5
     */
    private class PhotoSwipeAdapter extends PagedAdapter {
        
        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			switch (position) {
			case 0:
				convertView =this.getDescriptionView(); 
				break;
			case 1:
            	convertView = this.getProgressView();
				break;

			default:
				break;
			}
			return convertView;
		}
		
		/**
		 * @return 第一个page（即page0）的View，task基本信息的description
		 */
		private View getDescriptionView() {
			View view = LayoutInflater.from(TaskDetailsActivity.this).inflate(R.layout.taskdetails_paged_view_item1, null);

			EditText taskNameText = (EditText) view.findViewById(R.id.taskNameEditText);
			EditText taskMemoText = (EditText) view.findViewById(R.id.taskMemoEditText);
			TextView periodicInfoText = (TextView) view.findViewById(R.id.taskPeriodicInfoTextView);
			TextView startTimeText = (TextView) view.findViewById(R.id.taskStartTimeTextView);
			TextView deadlineText = (TextView) view.findViewById(R.id.taskDeadlineTextView);
			
			//TODO 完善VIEW1的交互

			return view;
		}
		
		/**
		 * @return 第二个page（即page1）的View，task任务进度、完成信息的展示
		 */
		private View getProgressView() {
			View view = LayoutInflater.from(TaskDetailsActivity.this).inflate(R.layout.taskdetails_paged_view_item2, null);
			//TODO 完善VIEW2的交互
			return view;
		}
    }

}
