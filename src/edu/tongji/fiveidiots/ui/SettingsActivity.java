package edu.tongji.fiveidiots.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.util.Settings;

/**
 * 设置界面
 * @author IRainbow5
 *
 */
public class SettingsActivity extends Activity {

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
	}
}
