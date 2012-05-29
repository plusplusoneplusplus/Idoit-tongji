package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.util.ActivityUtil;
import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


/**
 * TaskOverviewActivity是总览任务的类
 */
public class TaskOverviewActivity extends GDActivity {

	public TaskOverviewActivity(){
		super(ActionBar.Type.Normal);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.idoit);
        addActionBarItem(Type.Add);
        addActionBarItem(Type.Edit);
        
        Log.i("__ANDRIY__", "TaskOverview.onCreate()");
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		if (position == 0) {
			//第一个添加的东西，目前是add，当做进入计时器
			ActivityUtil.startNewActivity(this, PomotimerActivity.class, 0, false);
		}
		else {
			Toast.makeText(this, "bar_item at pos: " + position, Toast.LENGTH_SHORT).show();
		}
		return true;
	}
}
