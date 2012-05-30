package edu.tongji.fiveidiots.ui;

import edu.tongji.fiveidiots.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 专门辅助测试后台数据的类，方便输出到屏幕查看，通过主界面的“+”进入
 * @author Andriy
 */
public class TestingActivity extends Activity {

	private Button testButton1;
	private Button testButton2;
	private Button testButton3;
	private Button testButton4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.only_for_testing);
		
		this.testButton1 = (Button) findViewById(R.id.onlyForTestingButton1);
		this.testButton2 = (Button) findViewById(R.id.onlyForTestingButton2);
		this.testButton3 = (Button) findViewById(R.id.onlyForTestingButton3);
		this.testButton4 = (Button) findViewById(R.id.onlyForTestingButton4);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		this.testButton1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 测试用button1
				showToast("from button1");
			}
		});
		this.testButton2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 测试用button2
				showAlertDialog("from button2");
			}
		});
		this.testButton3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 测试用button3
				showToast("from button3");
			}
		});
		this.testButton4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 测试用button4
				showAlertDialog("from button4");
			}
		});
	}

	/**
	 * 在界面上生成一个Toast用于测试的输出
	 * @param message
	 */
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 在界面上生成一个AlertDialog用于测试的输出
	 * @param message
	 */
	private void showAlertDialog(String message) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("TESTING");
		builder.setMessage(message);
		builder.create().show();
	}
}
