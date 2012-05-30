package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.util.ActivityUtil;
import greendroid.app.GDActivity;
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
import android.view.View;
import android.widget.Toast;

/**
 * 抽离出Overview（总览任务）界面上ActionBar部分
 * 抽象类，继承后实现主界面
 * @author IRainbow5
 *
 */
public abstract class OverviewActionBarActivity extends GDActivity{
	
	//actionbar上按钮点击后弹出的grid
	private QuickActionGrid mGridMore;
	private QuickActionGrid mGridTimeLine;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mySetActionBarContentView();
		prepareActionBar();
		prepareQuickActionGrid();
	}
	
	//继承重写，设置view
	protected abstract void mySetActionBarContentView();
	
	//设置ActionBar
	private void prepareActionBar()
	{
		addActionBarItem(Type.Add, R.id.action_bar_add);
        addActionBarItem(Type.Eye, R.id.action_bar_timer);
        addActionBarItem(Type.List, R.id.action_bar_more);
	}
	
	//设置弹出的Grid
	private void prepareQuickActionGrid()
	{
		mGridMore = new QuickActionGrid(this);
		mGridMore.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_eye, R.string.exit));
		mGridMore.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_eye, R.string.exit));
		mGridMore.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_eye, R.string.exit));
		mGridMore.setOnQuickActionClickListener(mQuickActionListener);
	}
	
	//Grid上按钮的Listener
	private OnQuickActionClickListener mQuickActionListener = new OnQuickActionClickListener() {
		
		@Override
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
			Toast.makeText(OverviewActionBarActivity.this, "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
		}
	};
	
	//ActionBar上按钮的Listener
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
	
	//显示Grid
	public void onShowGrid(View v) {
        mGridMore.show(v);
    }
	
	//自定义QuickAction来适配白色按钮logo
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
