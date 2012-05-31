package edu.tongji.fiveidiots.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;


public class ClickForbiddenSD extends SlidingDrawer {
	
	private int mHandleId = 0;
	private static enum HandleTouchState{UP, MOVE, DOWN};
	private HandleTouchState mPreTouchState = HandleTouchState.UP;
	private boolean mIsClicked = false;
	
	public ClickForbiddenSD(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setHandleId(int resId){
		mHandleId = resId;
	}
	
	public int getHandleId(){
		return mHandleId;
	}

    /*
     * 获取控件的屏幕区域
     */
    private Rect getRectOnScreen(View view){
        Rect rect = new Rect();
        int[] location = new int[2];
        View parent = view;
        if(view.getParent() instanceof View){
            parent = (View)view.getParent();
        }
        parent.getLocationOnScreen(location);
        view.getHitRect(rect);
        rect.offset(location[0], location[1]);
        
        return rect;
    }
	
/*	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		Log.i("__Rainbow__", "onInterceptTouchEvent");
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			//Log.i("__Rainbow__", "hit DOWN");
			
			View view = findViewById(mHandleId);
			Rect rect = getRectOnScreen(view);
			
			//Log.i("__Rainbow__", String.format("%d, %d, %d, %d, -----%f, %f", rect.left, rect.right, rect.top, rect.bottom, event.getX(), event.getY()));
			
			if(rect.contains((int)event.getX(), (int)event.getY()))
			{
				Log.i("__Rainbow__", "hit handle");
				mPreTouchState = HandleTouchState.DOWN;
				//return false;
			}
			
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP && mPreTouchState == HandleTouchState.DOWN){
			mPreTouchState = HandleTouchState.UP;
			mIsClicked = true;
			Log.i("__Rainbow__", "click handle");
		}
		
		if(event.getAction() == MotionEvent.ACTION_MOVE && mPreTouchState == HandleTouchState.DOWN){
			mPreTouchState = HandleTouchState.MOVE;
			mIsClicked = false;
			Log.i("__Rainbow__", "move handle");
		}

		return super.onInterceptTouchEvent(event);
	}*/

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		MotionEvent.
		//Log.i("__Rainbow__", String.format("On touch %d", event.getAction()));
		
		//Log.i("__Rainbow__", "onInterceptTouchEvent");
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			//Log.i("__Rainbow__", "hit DOWN");
			
			View view = findViewById(mHandleId);
			Rect rect = getRectOnScreen(view);
			
			//Log.i("__Rainbow__", String.format("%d, %d, %d, %d, -----%f, %f", rect.left, rect.right, rect.top, rect.bottom, event.getRawX(), event.getRawY()));
			
			if(rect.contains((int)event.getRawX(), (int)event.getRawY()))
			{
				Log.i("__Rainbow__", "hit handle");
				mPreTouchState = HandleTouchState.DOWN;
				//return false;
			}
			
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP && mPreTouchState == HandleTouchState.DOWN){
			mPreTouchState = HandleTouchState.UP;
			mIsClicked = true;
			Log.i("__Rainbow__", "click handle");
		}
		
		if(event.getAction() == MotionEvent.ACTION_MOVE && mPreTouchState == HandleTouchState.DOWN){
			mPreTouchState = HandleTouchState.MOVE;
			mIsClicked = false;
			Log.i("__Rainbow__", "move handle");
		}
		
		if(mIsClicked == true){
			return true;
		}
		
		return super.onTouchEvent(event);
	}
}

