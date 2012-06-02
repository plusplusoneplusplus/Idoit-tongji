package edu.tongji.fiveidiots.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import edu.tongji.fiveidiots.R;

/**
 * 自己的relativelayout，实现全局触摸监听
 * @author IRainbow5
 *
 */


public class MyRelativeLayout extends RelativeLayout{

	SlidingDrawer mSlidingDrawer;
	
	public MyRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setSlidingDrawer(int resId){
		mSlidingDrawer = (SlidingDrawer) findViewById(resId);
	}
	
	View viewe = findViewById(R.id.task_list);
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		/*
		View view = findViewById(R.id.task_list);
		Log.i("__Rainbow__", String.format("task %d %d %d %d", view.getLeft(), view.getRight(), view.getTop(), view.getBottom()));
		//view.dispatchTouchEvent(ev);
		view = findViewById(R.id.sliding_drawer);
		Log.i("__Rainbow__", String.format("tag %d %d %d %d", view.getLeft(), view.getRight(), view.getTop(), view.getBottom()));
		//view.dispatchTouchEvent(ev);
		view = this;
		Log.i("__Rainbow__", String.format("this %d %d %d %d", view.getLeft(), view.getRight(), view.getTop(), view.getBottom()));
		*/
		
		//View view = findViewById(R.id.sliding_drawer);
		//int off = getRight() - findViewById(R.id.tag_handle).getRight();
		//ev.setLocation(ev.getX() - off, ev.getY());
		//Log.i("__Rainbow__", String.format("tag %d %d %d %d", view.getLeft(), view.getRight(), view.getTop(), view.getBottom()));
		//view.dispatchTouchEvent(ev);
		
		SlidingDrawer view = (SlidingDrawer)findViewById(R.id.sliding_drawer);
		Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
		if(!rect.contains((int)ev.getX(), (int)ev.getY()) && view.isOpened()){
			//Log.i("__Rainbow__", "fuck u");
			view.animateClose();
			//viewe = findViewById(R.id.taskListView);
			//viewe.d(ev);
			//return super.dispatchTouchEvent(ev);
		}
		//super.dispatchTouchEvent(ev);
		//Log.i("__Rainbow__", String.format("reg %f %f", ev.getX(), ev.getY()));
		//Log.i("__Rainbow__", String.format("rect %d %d %d %d", rect.left, rect.top, rect.right, rect.bottom));
		return super.dispatchTouchEvent(ev);
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
	
}
