package org.chenglei.widget.datepicker.Sample;

import org.chenglei.widget.datepicker.DatePicker;
import org.chenglei.widget.datepicker.Sound;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	private DatePicker mDatePicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mDatePicker = (DatePicker) findViewById(R.id.date_picker);
		
		Sound sound = new Sound(this);
		//sound.setCustomSound(R.raw.beep);
		mDatePicker.setSoundEffect(sound);
		mDatePicker.setSoundEffectsEnabled(true);
	}
}
