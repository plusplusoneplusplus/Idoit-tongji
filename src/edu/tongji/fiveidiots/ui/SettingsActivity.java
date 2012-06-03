package edu.tongji.fiveidiots.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.util.Settings;

/**
 * 设置界面
 * @author IRainbow5
 *
 */
public class SettingsActivity extends Activity {

	/**
	 * 选择铃声的判断标记
	 */
	private static final int REQUEST_CODE_PICK_RINGTONE = 1; 
	/**
	 * 显示设置值的textview
	 */
	private TextView mTV_Pomotime;
	private TextView mTV_ShortBreak;
	private TextView mTV_LongBreak;
	private TextView mTV_LongBreak_Interval;
	
	/**
	 * 设置值的seekbar
	 */
	private SeekBar mSB_Pomotime;
	private SeekBar mSB_ShortBreak;
	private SeekBar mSB_LongBreak;
	private SeekBar mSB_LongBreak_Interval;
	
	/**
	 * 是否震动的togglebutton
	 */
	private ToggleButton mTB_Vibrate;
	
	/**
	 * 选择铃声的button
	 */
	private Button mBTN_Ringtone;
	
	/**
	 * Pomotime的取值范围
	 */
	private final static int mPomotimeMin = 10;
	private final static int mPomotimeMax = 60;
	
	/**
	 * ShortBreak的取值范围
	 */
	private final static int mShortBreakMin = 1;
	private final static int mShortBreakMax = 10;
	
	/**
	 * LongBreak取值范围
	 */
	private final static int mLongBreakMin = 5;
	private final static int mLongBreakMax = 30;
	
	/**
	 * LongBreak Interval取值范围
	 */
	private final static int mLongBreakIntervalMin = 2;
	private final static int mLongBreakIntervalMax = 6;
	
	/**
	 * 设置实例
	 */
	private Settings mSettings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		mSettings = new Settings(this);
		
		widgetBinding();
		initWidgetValue();
		widgetListenerBinding();
	}
	
	/**
	 * 初始化控件绑定
	 */
	private void widgetBinding(){
		/**
		 * TextView绑定
		 */
		mTV_Pomotime = (TextView) findViewById(R.id.settings_pomotime_textview);
		mTV_ShortBreak = (TextView) findViewById(R.id.settings_shortbreak_textview);
		mTV_LongBreak = (TextView) findViewById(R.id.settings_longbreak_textview);
		mTV_LongBreak_Interval = (TextView) findViewById(R.id.settings_longbreak_interval_textview);
		
		/**
		 * SeekBar绑定
		 */
		mSB_Pomotime = (SeekBar) findViewById(R.id.settings_pomotime_seekbar);
		mSB_ShortBreak = (SeekBar) findViewById(R.id.settings_shortbreak_seekbar);
		mSB_LongBreak = (SeekBar) findViewById(R.id.settings_longbreak_seekbar);
		mSB_LongBreak_Interval = (SeekBar) findViewById(R.id.settings_longbreak_interval_seekbar);
		
		/**
		 * ToggleButton绑定
		 */
		mTB_Vibrate = (ToggleButton) findViewById(R.id.settings_vibrate_toggle);
		
		/**
		 * Button绑定
		 */
		mBTN_Ringtone = (Button) findViewById(R.id.settings_soundselect_btn);
		
		/**
		 * 设置值域
		 */
		mSB_Pomotime.setMax(mPomotimeMax - mPomotimeMin);
		mSB_ShortBreak.setMax(mShortBreakMax - mShortBreakMin);
		mSB_LongBreak.setMax(mLongBreakMax - mLongBreakMin);
		mSB_LongBreak_Interval.setMax(mLongBreakIntervalMax - mLongBreakIntervalMin);
		
	}
	
	/**
	 * 初始化控件值
	 */
	private void initWidgetValue() {
		
		int value;
		
		/**
		 * 初始化Pomotime
		 */
		value = mSettings.getPomotimerDuration();
		mTV_Pomotime.setText(SettingsActivity.this.getString(R.string.settings_pomotime) + 
				value
				+ SettingsActivity.this.getString(R.string.settings_minute));
		mSB_Pomotime.setProgress(value - mPomotimeMin);
		
		/**
		 * 初始化ShortBreak
		 */
		value = mSettings.getPomotimerInterval();
		mTV_ShortBreak.setText(SettingsActivity.this.getString(R.string.settings_shortbreak) + 
				value
				+ SettingsActivity.this.getString(R.string.settings_minute));
		mSB_ShortBreak.setProgress(value - mShortBreakMin);
		
		/**
		 * 初始化LongBreak
		 */
		value = mSettings.getPomotimerLongInterval();
		mTV_LongBreak.setText(SettingsActivity.this.getString(R.string.settings_longbreak) + 
				value
				+ SettingsActivity.this.getString(R.string.settings_minute));
		mSB_LongBreak.setProgress(value - mLongBreakMin);
		
		/**
		 * 初始化LongBreak Interval
		 */
		value = mSettings.getPomotimerCount();
		mTV_LongBreak_Interval.setText(SettingsActivity.this.getString(R.string.settings_longbreak_interval) + 
				value
				+ SettingsActivity.this.getString(R.string.settings_count));
		mSB_LongBreak_Interval.setProgress(value - mLongBreakIntervalMin);
		
		/**
		 * 初始化Notify Vibrate
		 */
		boolean b = mSettings.getPomotimerNotifyVibrate();
		mTB_Vibrate.setChecked(b);
		
		/**
		 * 初始化Notify Ringtone
		 */
		String str = RingtoneManager.getRingtone(this, mSettings.getPomotimerNotifyRingTone()).getTitle(this);
		mBTN_Ringtone.setText(this.getString(R.string.settings_sound) + cutFileType(str));
	}
	
	/**
	 * 绑定监听器
	 */
	private void widgetListenerBinding(){
		/**
		 * Pomotime SeekBar监听器
		 */
		mSB_Pomotime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mTV_Pomotime.setText(SettingsActivity.this.getString(R.string.settings_pomotime) + 
						String.format("%d", progress + mPomotimeMin)
						+ SettingsActivity.this.getString(R.string.settings_minute));
				mSettings.setPomotimerDuration(progress + mPomotimeMin);
				
			}
		});
		
		/**
		 * ShortBreak SeekBar监听器
		 */
		mSB_ShortBreak.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mTV_ShortBreak.setText(SettingsActivity.this.getString(R.string.settings_shortbreak) + 
						String.format("%d", progress + mShortBreakMin)
						+ SettingsActivity.this.getString(R.string.settings_minute));
				mSettings.setPomotimerInterval(progress + mShortBreakMin);
			}
		});
		
		/**
		 * LongBreak SeekBar监听器
		 */
		mSB_LongBreak.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mTV_LongBreak.setText(SettingsActivity.this.getString(R.string.settings_longbreak) + 
						String.format("%d", progress + mLongBreakMin)
						+ SettingsActivity.this.getString(R.string.settings_minute));
				mSettings.setPomotimerLongInterval(progress + mLongBreakMin);
				
			}
		});
		
		
		/**
		 * LongBreak Interval SeekBar监听器
		 */
		mSB_LongBreak_Interval.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mTV_LongBreak_Interval.setText(SettingsActivity.this.getString(R.string.settings_longbreak_interval) + 
						String.format("%d", progress + mLongBreakIntervalMin)
						+ SettingsActivity.this.getString(R.string.settings_count));
				mSettings.setPomotimerCount(progress + mLongBreakIntervalMin);
			}
		});
		
		/**
		 * Notify Vibrate ToggleButton监听器
		 */
		mTB_Vibrate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.i("__Rainbow__", "" + isChecked);
				mSettings.setPomotimerNotifyVibrate(isChecked);
			}
		});
		
		/**
		 * Ringtone Select Button监听器
		 */
		mBTN_Ringtone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pickRingtone();
			}
		});
	}
	
	private void pickRingtone() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		//添加 “默认”选项
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		
		//设置铃声类型
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
		
		//不显示静音
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
		
		Uri ringtongUri = mSettings.getPomotimerNotifyRingTone();
		
		//在已选铃声上打勾
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtongUri);
		
		//启动选择
		startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE);
	}

	/**
	 * 选择铃声后进入此函数
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK) {
			return;
		}
		
		switch(requestCode) {
		case REQUEST_CODE_PICK_RINGTONE: {
			Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			handleRingtonePicked(uri);
			break;
		}
		default:
			break;
		}
	}
	
	private void handleRingtonePicked(Uri uri) {
		mSettings.setPomotimerNotifyRingTome(uri);
		String str = RingtoneManager.getRingtone(this, uri).getTitle(this);
		mBTN_Ringtone.setText(this.getString(R.string.settings_sound) + cutFileType(str));
	}
	
	
	/**
	 * 删除铃声文件名中的后缀
	 * 如abc.ogg输出abc
	 */
	private String cutFileType(String str) {
		/**
		 * 如果输入null，输出"Unknown"
		 */
		if(str == null) {
			return "Unknown";
		}
		
		int i = str.lastIndexOf(".");
		if(i > 0) {
			return str.substring(0, i);
		}
		return str;
	}
}
