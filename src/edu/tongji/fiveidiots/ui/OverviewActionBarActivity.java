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

import java.util.ArrayList;

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
	
	private View mHomeButton;
	
	/**
	 * 存储grid上 postion-resID键值对 数组
	 */
	private ArrayList<Integer> mGridMoreArray = new ArrayList<Integer>();
	private ArrayList<Integer> mGridTimeLineArray = new ArrayList<Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mySetActionBarContentView();
		prepareActionBar();
		prepareQuickActionGrid();
		
		mHomeButton = getActionBar().getHomeButton();
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
		//设置“更多操作”按钮
		mGridMore = new QuickActionGrid(this);
		QuickActionHelper quickActionHelper = new QuickActionHelper(mGridMoreArray, mGridMore);
		quickActionHelper.addQuickAction(R.drawable.gd_action_bar_eye, R.string.analysis);
		quickActionHelper.addQuickAction(R.drawable.gd_action_bar_eye, R.string.settings);
		quickActionHelper.addQuickAction(R.drawable.gd_action_bar_eye, R.string.about);
		quickActionHelper.addQuickAction(R.drawable.gd_action_bar_eye, R.string.exit);
		mGridMore.setOnQuickActionClickListener(mQuickActionMoreListener);
		
		//设置“时间线”按钮
		mGridTimeLine = new QuickActionGrid(this);
		quickActionHelper.config(mGridTimeLineArray, mGridTimeLine);
		quickActionHelper.addQuickAction(R.drawable.gd_action_bar_eye, R.string.today);
		quickActionHelper.addQuickAction(R.drawable.gd_action_bar_eye, R.string.future);
		quickActionHelper.addQuickAction(R.drawable.gd_action_bar_eye, R.string.periodic);
		quickActionHelper.addQuickAction(R.drawable.gd_action_bar_eye, R.string.pool);
		quickActionHelper.addQuickAction(R.drawable.gd_action_bar_eye, R.string.all);
		mGridTimeLine.setOnQuickActionClickListener(mQuickActionTimeLineListener);
	}
	
	private class QuickActionHelper {
		private ArrayList<Integer> mGridArray;
		private QuickActionGrid mGrid;
		
		QuickActionHelper(ArrayList<Integer> arr, QuickActionGrid grid) {
			mGridArray = arr;
			mGrid = grid;
		}
		
		public void config(ArrayList<Integer> arr, QuickActionGrid grid) {
			mGridArray = arr;
			mGrid = grid;
		}

		//添加qucikaction，并填写键值对，供listener使用
		public void addQuickAction(int drawableID, int titleID) {
			mGrid.addQuickAction(new MyQuickAction(OverviewActionBarActivity.this, drawableID, titleID));
			mGridArray.add(titleID);
		}
	}
	
	//GridMore上按钮的Listener
	private OnQuickActionClickListener mQuickActionMoreListener = new OnQuickActionClickListener() {

		@Override
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
			switch(mGridMoreArray.get(position))
			{
			case R.string.analysis:
				break;
			case R.string.settings:
				ActivityUtil.startNewActivity(OverviewActionBarActivity.this, SettingsActivity.class, 0L, false);
				break;
			case R.string.about:
				break;
			case R.string.exit:
				break;
			default:
				break;
			}
		}
		
	};
	
	//GridTimeLine上按钮的Listener
	private OnQuickActionClickListener mQuickActionTimeLineListener = new OnQuickActionClickListener() {
		
		@Override
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
			switch(mGridTimeLineArray.get(position))
			{
			case R.string.today:
				Toast.makeText(OverviewActionBarActivity.this, "today", Toast.LENGTH_SHORT).show();
				break;
			case R.string.future:
				break;
			case R.string.periodic:
				break;
			case R.string.pool:
				Toast.makeText(OverviewActionBarActivity.this, "pool", Toast.LENGTH_SHORT).show();
				break;
			case R.string.all:
				break;
			default:
				break;
			}
		}
	};
	
	//ActionBar上按钮的Listener
	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		
		//if press HOME actionbar item, the 'item' value is null, so catch it here
		if(position == OnActionBarListener.HOME_ITEM)
		{
			mGridTimeLine.show(mHomeButton);
			return true;
		}

		switch(item.getItemId())
		{
		case R.id.action_bar_add:
			//=====跳转到测试后台数据用的界面=====
			ActivityUtil.startNewActivity(this, TestingActivity.class, 0, false);
			break;
		case R.id.action_bar_timer:
			ActivityUtil.startNewActivity(this, PomotimerActivity.class, 0, false);
			break;
		case R.id.action_bar_more:
			mGridMore.show((item.getItemView()));
			break;
		default:
			break;	
		}
		return true;
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
