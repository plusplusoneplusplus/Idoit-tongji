package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.util.ActivityUtil;
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
import android.view.View;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.idoit);
        
        prepareActionBar();
        prepareQuickActionGrid();
        
        Log.i("__ANDRIY__", "TaskOverview.onCreate()");
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
}
