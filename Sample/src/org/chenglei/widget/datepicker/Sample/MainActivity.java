package org.chenglei.widget.datepicker.Sample;

import org.chenglei.widget.datepicker.DatePicker;
import org.chenglei.widget.datepicker.Sound;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private DatePicker mDatePicker1;
	private DatePicker mDatePicker2;
	private DatePicker mDatePicker3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mDatePicker1 = (DatePicker) findViewById(R.id.date_picker1);
		mDatePicker2 = (DatePicker) findViewById(R.id.date_picker2);
		mDatePicker3 = (DatePicker) findViewById(R.id.date_picker3);
		
		Sound sound1 = new Sound(this);
		mDatePicker1.setSoundEffect(sound1)
			.setTextColor(Color.RED)
			.setFlagTextColor(Color.RED)
			.setTextSize(25)
			.setFlagTextSize(15)
			.setSoundEffectsEnabled(true);
		
		Sound sound2 = new Sound(this);
		sound2.setCustomSound(R.raw.beep);
		mDatePicker2.setSoundEffect(sound2)
			.setTextColor(Color.WHITE)
			.setFlagTextColor(Color.WHITE)
			.setTextSize(25)
			.setFlagTextSize(15)
			.setBackground(Color.BLACK)
			.setSoundEffectsEnabled(true);
		
		mDatePicker3.setTextColor(Color.BLACK)
			.setFlagTextColor(Color.BLACK)
			.setBackground(Color.RED)
			.setTextSize(25)
			.setFlagTextSize(15)
			.setRowNumber(5)
			.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
				
				@Override
				public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					Log.i("DatePicker", year + "-" + monthOfYear + "-" + dayOfMonth);
				}
			});
	}
}
