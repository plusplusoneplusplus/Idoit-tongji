package edu.tongji.fiveidiots;

import greendroid.app.GDActivity;
import greendroid.widget.ActionBar;
import greendroid.widget.ActionBar.OnActionBarListener;
import greendroid.widget.ActionBarItem;
import greendroid.widget.ActionBarItem.Type;
import android.os.Bundle;
import android.widget.Toast;


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
	}
	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		
		
		
		
		Toast.makeText(this, "ffff", Toast.LENGTH_SHORT).show();
		return true;
	}
}
