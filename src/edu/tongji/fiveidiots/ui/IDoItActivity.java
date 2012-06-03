package edu.tongji.fiveidiots.ui;

import android.app.Activity;
import android.os.Bundle;
import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.util.ActivityUtil;
import edu.tongji.fiveidiots.util.Settings;

/**
 * IDoItActivity是预留用来显示教学界面、介绍界面或App Logo的
 * 目前没起什么作用
 * @author IRainbow5
 */
public class IDoItActivity extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ActivityUtil.startNewActivity(this, OverviewTaskListActivity.class, 1000L, true);
        //ActivityUtil.startNewActivity(this, SettingsActivity.class, 1000L, true);
    }
}