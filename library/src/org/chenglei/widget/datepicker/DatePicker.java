package org.chenglei.widget.datepicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class DatePicker extends LinearLayout implements NumberPicker.OnValueChangeListener {
	
	private NumberPicker mYearPicker;
	private NumberPicker mMonthPicker;
	private NumberPicker mDayOfMonthPicker;
	
	private Calendar mCalendar;
	
	private OnDateChangedListener mOnDateChangedListener;
	
	private LayoutInflater mLayoutInflater;

	public DatePicker(Context context) {
		this(context, null);
	}
	
	public DatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		init();
	}
	
	private void init() {
		mLayoutInflater.inflate(R.layout.date_picker_layout, this, true);
		mYearPicker = (NumberPicker) findViewById(R.id.year_picker);
		mMonthPicker = (NumberPicker) findViewById(R.id.month_picker);
		mDayOfMonthPicker = (NumberPicker) findViewById(R.id.day_picker);
		
		mYearPicker.setOnValueChangeListener(this);
		mMonthPicker.setOnValueChangeListener(this);
		mDayOfMonthPicker.setOnValueChangeListener(this);
		
		if (getResources().getConfiguration().locale != Locale.CHINESE 
				&& getResources().getConfiguration().locale != Locale.TAIWAN
				&& getResources().getConfiguration().locale != Locale.TRADITIONAL_CHINESE) {
			
			String[] monthNames = getResources().getStringArray(R.array.month_name);
			mMonthPicker.setCustomTextArray(monthNames);
			
		}
		
		mCalendar = Calendar.getInstance();
		setDate(mCalendar.getTime());
	}
	
	public void setDate(Date date) {
		mCalendar.setTime(date);
		mDayOfMonthPicker.setEndNumber(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		mYearPicker.setCurrentNumber(mCalendar.get(Calendar.YEAR));
		mMonthPicker.setCurrentNumber(mCalendar.get(Calendar.MONTH) + 1);
		mDayOfMonthPicker.setCurrentNumber(mCalendar.get(Calendar.DAY_OF_MONTH));
		
	}

	@Override
	public void onValueChange(final NumberPicker picker, final int oldVal, final int newVal) {
		
		if (picker == mYearPicker) {
			mCalendar.set(Calendar.YEAR, newVal);
			mDayOfMonthPicker.setEndNumber(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		} else if (picker == mMonthPicker) {
			mCalendar.set(Calendar.MONTH, newVal - 1);
			mDayOfMonthPicker.setEndNumber(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		} else if (picker == mDayOfMonthPicker) {
			mCalendar.set(Calendar.DAY_OF_WEEK, newVal);
		}
		
		notifyDateChanged();
	}
	
	/**
     * The callback used to indicate the user changes\d the date.
     */
    public interface OnDateChangedListener {

        /**
         * Called upon a date change.
         *
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *            with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth);
    }
    
    public void setOnDateChangedListener(OnDateChangedListener l) {
    	mOnDateChangedListener = l;
    }
    
    private void notifyDateChanged() {
    	if (mOnDateChangedListener != null) {
    		mOnDateChangedListener.onDateChanged(this, getYear(), getMonth(), getDayOfMonth());
    	}
    }
    
    public int getYear() {
    	return mCalendar.get(Calendar.YEAR);
    }
    
    public int getMonth() {
    	return mCalendar.get(Calendar.MONTH) + 1;
    }
    
    public int getDayOfMonth() {
    	return mCalendar.get(Calendar.DAY_OF_MONTH);
    }
    
    public void setSoundEffect(Sound sound) {
    	mYearPicker.setSoundEffect(sound);
    	mMonthPicker.setSoundEffect(sound);
    	mDayOfMonthPicker.setSoundEffect(sound);
    }
    
    @Override
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
    	super.setSoundEffectsEnabled(soundEffectsEnabled);
    	mYearPicker.setSoundEffectsEnabled(soundEffectsEnabled);
    	mMonthPicker.setSoundEffectsEnabled(soundEffectsEnabled);
    	mDayOfMonthPicker.setSoundEffectsEnabled(soundEffectsEnabled);
    }

}
