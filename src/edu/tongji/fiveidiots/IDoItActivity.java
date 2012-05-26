/*
 * IDoItActivity是预留用来显示教学界面、介绍界面或App Logo的
 * 目前没起什么作用
 */

package edu.tongji.fiveidiots;

import android.app.Activity;
import android.os.Bundle;
import edu.tongji.util.ActivityUtil;


public class IDoItActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idoit);
        
        ActivityUtil.startNewActivity(this, TaskOverviewActivity.class, 1000L, true);
    }
}