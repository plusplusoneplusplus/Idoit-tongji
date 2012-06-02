package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * 展现任务的详细细节
 * 及详细细节的修改
 * @author Andriy
 */
public class TaskDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.task_detail);
	}

}
