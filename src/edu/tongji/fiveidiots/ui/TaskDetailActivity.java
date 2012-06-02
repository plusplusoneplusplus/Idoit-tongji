package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.ctrl.TaskInfo;
import edu.tongji.fiveidiots.util.TestingHelper;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 展现任务的详细细节
 * 及详细细节的修改
 * @author Andriy
 */
public class TaskDetailActivity extends Activity {

	private TextView textView;
	private TaskInfo task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null) {
			throw new IllegalStateException("没有传递一个带有TASK_ID的bundle怎么可以到TaskDetailActivity来！");
		}
		if (bundle != null) {
			long taskID = bundle.getLong(OverviewTaskListActivity.TASK_ID_STR, -1);
			//TODO 测试时，直接随机生成一个task来用，到时候要新开一个线程异步取data，然后刷新
			task = TestingHelper.getRandomTask();
			task.setId(taskID);
		}
		
		this.setContentView(R.layout.task_detail);
		textView = (TextView) findViewById(R.id.textView1);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		textView.setText(task.getId() + " " + task.getName());
	}
	
	

}
