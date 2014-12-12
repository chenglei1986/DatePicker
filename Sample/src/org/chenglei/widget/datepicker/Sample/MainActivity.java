package org.chenglei.widget.datepicker.Sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "CHENGLEI_TEST";
	
//	private PickerView mPickerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
//		mPickerView = (PickerView) findViewById(R.id.number_picker);
//		mPickerView.setStartNumber(0);
//		mPickerView.setEndNumber(100);
//		mPickerView.setCurrentNumber(50);
//		
//		mPickerView.setOnScrollListener(new OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChange(PickerView view, int scrollState) {
//				//System.out.println(view.getCurrentNumber());
//			}
//		});
//		
//		mPickerView.setOnValueChangeListener(new OnValueChangeListener() {
//			
//			@Override
//			public void onValueChange(PickerView picker, int oldVal, int newVal) {
//				System.out.println(newVal);
//			}
//		});
	}

	@Override
	public void onClick(final View v) {
		
		
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "onDestroy");
	}
	
}
