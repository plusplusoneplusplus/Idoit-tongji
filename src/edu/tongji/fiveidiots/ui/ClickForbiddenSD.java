package edu.tongji.fiveidiots.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;

/**
 * 自定义slidingdrawer，使他只能拖拽无法点击
 * @author IRainbow5
 *
 */

public class ClickForbiddenSD extends SlidingDrawer {

	private int mPerAction = MotionEvent.ACTION_UP;
	private boolean mActionInHandle = false;
	private MotionEvent mPreEvent;
	
	public ClickForbiddenSD(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		View view = this.getHandle();
		Rect rect = getRectOnScreen(view);
		mActionInHandle = rect.contains((int)event.getRawX(), (int)event.getRawY());
		return super.onInterceptTouchEvent(event);
	}

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
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();
		
		if(mActionInHandle == false){
			mPerAction = action;
			return false;
		}

		if(action == MotionEvent.ACTION_UP && mPerAction == MotionEvent.ACTION_DOWN){
			mPerAction = action;
			return true;
		}
		
		if(action == MotionEvent.ACTION_MOVE && mPerAction == MotionEvent.ACTION_DOWN){
			mPerAction = action;
			super.onTouchEvent(mPreEvent);
			super.onTouchEvent(event);
			return true;
		}
		
		
		mPerAction = action;
		if(action == MotionEvent.ACTION_DOWN){
			mPreEvent = event;
			return true;
		}
		
		return super.onTouchEvent(event);
	}
	
	
}