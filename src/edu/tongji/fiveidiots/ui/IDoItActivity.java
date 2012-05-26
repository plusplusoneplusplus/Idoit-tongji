package edu.tongji.fiveidiots.ui;

import android.app.Activity;
import android.os.Bundle;
import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.util.ActivityUtil;


public class IDoItActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idoit);
        
//        ActivityUtil.startNewActivity(this, TaskOverviewActivity.class, 1000L, true);
        /*
         * testing, Andriy
         */
        ActivityUtil.startNewActivity(this, PomoTimerActivity.class, 1000L, true);
    }
}