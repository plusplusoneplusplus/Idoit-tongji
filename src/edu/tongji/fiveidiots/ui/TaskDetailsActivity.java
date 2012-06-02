package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ctrl.TaskInfo;
import edu.tongji.fiveidiots.util.TestingHelper;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar.Type;
import greendroid.widget.PageIndicator;
import greendroid.widget.PagedAdapter;
import greendroid.widget.PagedView;
import greendroid.widget.PagedView.OnPagedViewChangeListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;


/**
 * 任务详细信息界面
 * 添加多个详细信息界面时修改PAGE_COUNT PAGE_MAX_INDEX 以及 getView方法
 * 在res/layout中添加对应界面布局文件
 * 其它不用修改
 * @author Andriy  @author IRainbow5
 */

public class TaskDetailsActivity extends GDActivity {
	
	private static final int PAGE_COUNT = 2;
	private static final int PAGE_MAX_INDEX = PAGE_COUNT - 1;
	
	//小点点
	private PageIndicator mPageIndicatorNext;
	private PageIndicator mPageIndicatorPrev;
	private PageIndicator mPageIndicatorOther;
	
	
	private TaskInfo task;
	
	//actionbar为空
	public TaskDetailsActivity() {
		super(Type.Empty);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null) {
			throw new IllegalStateException("没有传递一个带有TASK_ID的bundle怎么可以到TaskDetailActivity来！");
		}
		if (bundle != null) {
			long taskID = bundle.getLong(OverviewTaskListActivity.TASK_ID_STR, -1);
			//TODO 测试时，直接随机生成一个task来用，到时候要新开一个线程异步取data，然后刷新
			task = TestingHelper.getRandomTask();
			task.setId(taskID);
		}
        
        setActionBarContentView(R.layout.taskdetails_paged_view);

        this.setTitle("任务详细信息" + task.getId());
        
        final PagedView pagedView = (PagedView) findViewById(R.id.paged_view);
        pagedView.setOnPageChangeListener(mOnPagedViewChangedListener);
        pagedView.setAdapter(new PhotoSwipeAdapter());

        mPageIndicatorNext = (PageIndicator) findViewById(R.id.page_indicator_next);
        mPageIndicatorNext.setDotCount(PAGE_MAX_INDEX);
        mPageIndicatorNext.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pagedView.smoothScrollToNext();
            }
        });

        mPageIndicatorPrev = (PageIndicator) findViewById(R.id.page_indicator_prev);
        mPageIndicatorPrev.setDotCount(PAGE_MAX_INDEX);
        mPageIndicatorPrev.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pagedView.smoothScrollToPrevious();
            }
        });
        
        mPageIndicatorOther = (PageIndicator) findViewById(R.id.page_indicator_other);
        mPageIndicatorOther.setDotCount(PAGE_COUNT);
        
        setActivePage(pagedView.getCurrentPage());
    }
    
    private void setActivePage(int page) {
        mPageIndicatorOther.setActiveDot(page);
        mPageIndicatorNext.setActiveDot(PAGE_MAX_INDEX - page);
        mPageIndicatorPrev.setActiveDot(page);
    }
    
    private OnPagedViewChangeListener mOnPagedViewChangedListener = new OnPagedViewChangeListener() {

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

        public View getView(int position, View convertView, ViewGroup parent) {
        	/**
        	 * 在此设置每一个设置界面的id
        	 */
            switch(position)
            {
            case 0:
            	convertView = getLayoutInflater().inflate(R.layout.taskdetails_paged_view_item1, parent, false);
            	break;
            case 1:
            	convertView = getLayoutInflater().inflate(R.layout.taskdetails_paged_view_item2, parent, false);
            	break;
            default:
            	break;
            }
            return convertView;
        }

    }
}
