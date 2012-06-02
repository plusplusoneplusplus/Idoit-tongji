package edu.tongji.fiveidiots.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;

/**
 * 自己的relativelayout，实现全局触摸监听
 * @author IRainbow5
 *
 */


public class MyRelativeLayout extends RelativeLayout{

	//抽屉对象
	private SlidingDrawer mSlidingDrawer;
	//抽屉所在矩形框
	private Rect mSDRect;
	
	public MyRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置抽屉对象
	 * @param resId
	 */
	public void setSlidingDrawer(int resId){
		mSlidingDrawer = (SlidingDrawer) findViewById(resId);
		mSDRect = new Rect();
	}
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		if(mSDRect == null || mSlidingDrawer == null){
			return super.dispatchTouchEvent(ev);
		}
		
		//记录当前slidingdrawer位置
		mSDRect.set(mSlidingDrawer.getLeft(), mSlidingDrawer.getTop(), mSlidingDrawer.getRight(), mSlidingDrawer.getBottom());
		//判断触碰点是否在slidingdrawer矩形框内，如果不是并且slidingdrawer打开，则把它关闭
		if(!mSDRect.contains((int)ev.getX(), (int)ev.getY()) && mSlidingDrawer.isOpened()){
			Log.i("__Rainbow__", String.format("%d %d", (int)ev.getX(), (int)ev.getY()));
			Log.i("__Rainbow__", String.format("%d %d %d %d", mSDRect.left, mSDRect.right, mSDRect.top, mSDRect.bottom));
			
			
			mSlidingDrawer.animateClose();
			return true;
		}
		
		return super.dispatchTouchEvent(ev);
	}
	
}
