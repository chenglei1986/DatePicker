package org.chenglei.widget.datepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.view.ViewConfigurationCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public class NumberPicker extends View {

	private int mWidth;
	private int mHeight;
	
	private TextPaint mTextPaintHighLight;
	private TextPaint mTextPaintNormal;
	private TextPaint mTextPaintFlag;
	private Paint mPaintHighLightRect;
	private Paint mBackgroundPaint;
	
	private Paint mLinePaint;
	private Rect mTextBoundsHighLight;
	private Rect mTextBoundsNormal;
	private NumberHolder[] mTextYAxisArray = new NumberHolder[7];
	private int mStartYPos;
	private int mEndYPos;
	
	/**
     * The coefficient by which to adjust (divide) the max fling velocity.
     */
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
	
	private static final int DEFAULT_TEXT_COLOR_HIGH_LIGHT = Color.rgb(0, 150, 136);
	private static final int DEFAULT_TEXT_COLOR_NORMAL = Color.rgb(0, 150, 136);
	private static final int DEFAULT_FLAG_TEXT_COLOR = Color.rgb(0, 150, 136);
	private static final float DEFAULT_TEXT_SIZE_HIGH_LIGHT = 36;
	private static final float DEFAULT_TEXT_SIZE_NORMAL = 32;
	private static final float DEFAULT_FLAG_TEXT_SIZE = 24;
	
	private static final int DEFAULT_VERTICAL_SPACING = 16;
	
	private int mTextColorHighLight;
	private int mTextColorNormal;
	private float mTextSizeHighLight;
	private float mTextSizeNormal;
	
	private int mStartNumber;
	private int mEndNumber;
	private int mCurrentNumber;
	private int mVerticalSpacing;
	
	private String mFlagText;
	private int mFlagTextColor;
	private float mFlagTextSize;
	
	private int[] mNumberArray;
	private int mCurrNumPos;
	private int mTouchSlop;
	
	private RectF mHighLightRect;
	private Rect mTextBoundsFlag;
	
	private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
	private int mTouchAction = MotionEvent.ACTION_CANCEL;
	
	private Scroller mFlingScroller;
	private Scroller mAdjustScroller;
	private int mMinimumFlingVelocity;
	private int mMaximumFlingVelocity;
	
	public NumberPicker(Context context) {
		this(context, null);
	}

	public NumberPicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		readAttrs(context, attrs, defStyleAttr);
		init();
	}
	
	private void readAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberPicker, defStyleAttr, 0);
		
		mTextColorHighLight = a.getColor(R.styleable.NumberPicker_textColorHighLight, DEFAULT_TEXT_COLOR_HIGH_LIGHT);
		mTextColorNormal = a.getColor(R.styleable.NumberPicker_textColorNormal, DEFAULT_TEXT_COLOR_NORMAL);
		mTextSizeHighLight = a.getDimension(R.styleable.NumberPicker_textSizeHighLight, DEFAULT_TEXT_SIZE_HIGH_LIGHT);
		mTextSizeNormal = a.getDimension(R.styleable.NumberPicker_textSizeNormal, DEFAULT_TEXT_SIZE_NORMAL);
		mStartNumber = a.getInteger(R.styleable.NumberPicker_startNumber, 0);
		mEndNumber = a.getInteger(R.styleable.NumberPicker_endNumber, 0);
		mCurrentNumber = a.getInteger(R.styleable.NumberPicker_currentNumber, 0);
		mVerticalSpacing = (int) a.getDimension(R.styleable.NumberPicker_verticalSpacing, DEFAULT_VERTICAL_SPACING);
		
		mFlagText = a.getString(R.styleable.NumberPicker_flagText);
		mFlagTextColor = a.getColor(R.styleable.NumberPicker_flagTextColor, DEFAULT_FLAG_TEXT_COLOR);
		mFlagTextSize = a.getDimension(R.styleable.NumberPicker_flagTextSize, DEFAULT_FLAG_TEXT_SIZE);
	}

	private void init() {
		verifyNumber();
		initPaint();
		initRects();
		measureText();
		
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
		mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity() / SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT;
        
		mFlingScroller = new Scroller(getContext(), null, true);
		mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
	}
	
	private void verifyNumber() {
		if (mStartNumber < 0 || mEndNumber < 0) {
			throw new IllegalArgumentException("number can not be negative");
		}
		if (mStartNumber > mEndNumber) {
			mEndNumber = mStartNumber;
		}
		if (mCurrentNumber < mStartNumber) {
			mCurrentNumber = mStartNumber;
		}
		if (mCurrentNumber > mEndNumber) {
			mCurrentNumber = mEndNumber;
		}
		
		mNumberArray = new int[mEndNumber - mStartNumber + 1];
		for (int i = 0; i < mNumberArray.length; i++) {
			mNumberArray[i] = mStartNumber + i;
		}
		
		mCurrNumPos = mCurrentNumber - mStartNumber;
	}
	
	private void initPaint() {
		mTextPaintHighLight = new TextPaint();
		mTextPaintHighLight.setTextSize(mTextSizeHighLight);
		mTextPaintHighLight.setColor(mTextColorHighLight);
		mTextPaintHighLight.setFlags(TextPaint.ANTI_ALIAS_FLAG);
		mTextPaintHighLight.setTextAlign(Align.CENTER);
		
		mTextPaintNormal = new TextPaint();
		mTextPaintNormal.setTextSize(mTextSizeNormal);
		mTextPaintNormal.setColor(mTextColorNormal);
		mTextPaintNormal.setFlags(TextPaint.ANTI_ALIAS_FLAG);
		mTextPaintNormal.setTextAlign(Align.CENTER);
		
		mTextPaintFlag = new TextPaint();
		mTextPaintFlag.setTextSize(mFlagTextSize);
		mTextPaintFlag.setColor(mFlagTextColor);
		mTextPaintFlag.setFlags(TextPaint.ANTI_ALIAS_FLAG);
		mTextPaintFlag.setTextAlign(Align.LEFT);
		
		mPaintHighLightRect = new Paint();
		mPaintHighLightRect.setColor(mTextColorHighLight);
		mPaintHighLightRect.setStyle(Paint.Style.STROKE);
		mPaintHighLightRect.setStrokeWidth(5);
		
		mBackgroundPaint = new Paint();
		
		
		mLinePaint = new Paint();
		mLinePaint.setColor(mTextColorHighLight);
	}
	
	private void initRects() {
		mTextBoundsHighLight = new Rect();
		mTextBoundsNormal = new Rect();
		mTextBoundsFlag = new Rect();
	}
	
	private void measureText() {
		
		String text = String.valueOf(mEndNumber);
		int length = text.length();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			builder.append("0");
		}
		text = builder.toString();
		mTextPaintHighLight.getTextBounds(text, 0, text.length(), mTextBoundsHighLight);
		mTextPaintNormal.getTextBounds(text, 0, text.length(), mTextBoundsNormal);
		if (mFlagText != null) {
			mTextPaintFlag.getTextBounds(mFlagText, 0, mFlagText.length(), mTextBoundsFlag);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		//canvas.drawColor(Color.WHITE);
		canvas.drawLine(0, mHighLightRect.top, mWidth, mHighLightRect.top, mPaintHighLightRect);
		canvas.drawLine(0, mHighLightRect.bottom, mWidth, mHighLightRect.bottom, mPaintHighLightRect);
		
		if (mFlagText != null) {
			int x = (mWidth + mTextBoundsHighLight.width() + 6) / 2;
			canvas.drawText(mFlagText, x, mHeight / 2, mTextPaintFlag);
		}
		
		for (int i = 0; i < mTextYAxisArray.length; i++) {
			if (mTextYAxisArray[i].mmIndex >= 0 && mTextYAxisArray[i].mmIndex <= mEndNumber - mStartNumber) {
				canvas.drawText(
						String.valueOf(mNumberArray[mTextYAxisArray[i].mmIndex]),
						mWidth / 2,
						mTextYAxisArray[i].mmPos + mTextBoundsNormal.height() / 2,
						mTextPaintNormal);
			}
		}
		
		canvas.drawRect(0, 0, mWidth, mHeight, mBackgroundPaint);
		
		if (MotionEvent.ACTION_UP == mTouchAction && mFlingScroller.isFinished()) {
			adjustYPosition();
		}
		
	}
	
	private void adjustYPosition() {
		if (mAdjustScroller.isFinished()) {
			mStartY = 0;
			int offsetIndex = Math.round((float)(mTextYAxisArray[0].mmPos - mStartYPos) / (float)mSpacing);
			int position = mStartYPos + offsetIndex * mSpacing;
			int dy = position - mTextYAxisArray[0].mmPos;
			if (dy != 0) {
				mAdjustScroller.startScroll(0, 0, 0, dy, 300);
			}
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);  
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);  
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);  
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.  
        	mWidth = widthSize;
        } else {
        	mWidth = mTextBoundsHighLight.width() + getPaddingLeft() + getPaddingRight() + mTextBoundsFlag.width() + 6;
        }
        
        if (heightMode == MeasureSpec.EXACTLY) {
        	mHeight = heightSize;
        } else {
        	mHeight = 5 * mTextBoundsNormal.height() + 4 * mVerticalSpacing + getPaddingTop() + getPaddingBottom();
        }
		
		setMeasuredDimension(mWidth, mHeight);
		
		
		if (null == mHighLightRect) {
			
			Shader shader = new LinearGradient(0, 0, 0, mHeight, new int[] {
					0xDFFFFFFF,
					0xCFFFFFFF, 
					0x00FFFFFF,
					0xCFFFFFFF,
					0xDFFFFFFF }, 
					null, Shader.TileMode.CLAMP);
			mBackgroundPaint.setShader(shader);
			mSpacing = mVerticalSpacing + mTextBoundsNormal.height();
			mStartYPos = mHeight / 2 - 3 * mSpacing;
			mEndYPos = mHeight / 2 + 3 * mSpacing;

			initTextYAxisArray();
			
			mHighLightRect = new RectF();
			mHighLightRect.left = 0;
			mHighLightRect.right = mWidth;
			mHighLightRect.top = (mHeight - mTextBoundsHighLight.height() - mVerticalSpacing) / 2;
			mHighLightRect.bottom = (mHeight + mTextBoundsHighLight.height() + mVerticalSpacing) / 2;
		}
	}
	
	private void initTextYAxisArray() {
		for (int i = 0; i < mTextYAxisArray.length; i++) {
			NumberHolder textYAxis = new NumberHolder(mCurrNumPos - 3 + i, mStartYPos + i * mSpacing);
			if (textYAxis.mmIndex > mNumberArray.length - 1) {
				textYAxis.mmIndex -= mNumberArray.length;
			} else if (textYAxis.mmIndex < 0) {
				textYAxis.mmIndex += mNumberArray.length;
			}
			mTextYAxisArray[i] = textYAxis;
		}
	}
	
	private int mStartY;
	private int mCurrY;
	private int mOffectY;
	private int mSpacing;
	private boolean mCanScroll;
	private VelocityTracker mVelocityTracker;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled()) {
            return false;
        }
		if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
		
		int action = event.getActionMasked();
		mTouchAction = action;
		if (MotionEvent.ACTION_DOWN == action) {
			mStartY = (int) event.getY();
			if (!mFlingScroller.isFinished() || !mAdjustScroller.isFinished()) {
				mFlingScroller.forceFinished(true);
				mAdjustScroller.forceFinished(true);
				onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			}
		} else if (MotionEvent.ACTION_MOVE == action) {
			mCurrY = (int) event.getY();
			mOffectY = mCurrY - mStartY;
			
			if (!mCanScroll && Math.abs(mOffectY) < mTouchSlop) {
				return false;
			} else {
				mCanScroll = true;
				if (mOffectY > mTouchSlop) {
					mOffectY -= mTouchSlop;
				} else if (mOffectY < -mTouchSlop) {
					mOffectY += mTouchSlop;
				}
			}

			mStartY = mCurrY;
			
			computeYPos(mOffectY);
			
			onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
			invalidate();
		} else if (MotionEvent.ACTION_UP == action) {
			mCanScroll = false;
			
			VelocityTracker velocityTracker = mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
            int initialVelocity = (int) velocityTracker.getYVelocity();
            
            if (Math.abs(initialVelocity) > mMinimumFlingVelocity) {
                fling(initialVelocity);
                onScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
            } else {
            	adjustYPosition();
            	invalidate();
            }
            mVelocityTracker.recycle();
            mVelocityTracker = null;
		}
		
		return true;
	}
	
	@Override
	public void computeScroll() {
		
		Scroller scroller = mFlingScroller;
		if (scroller.isFinished()) {
			onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			scroller = mAdjustScroller;
			if (scroller.isFinished()) {
				return;
			}
		}
		
		scroller.computeScrollOffset();	
		
		mCurrY = scroller.getCurrY();
		mOffectY = mCurrY - mStartY;
		
		computeYPos(mOffectY);
		
		invalidate();
		mStartY = mCurrY;
	}
	
	private void computeYPos(int offectY) {
		for (int i = 0; i < mTextYAxisArray.length; i++) {

			mTextYAxisArray[i].mmPos += offectY;
			if (mTextYAxisArray[i].mmPos >= mEndYPos + mSpacing) {
				mTextYAxisArray[i].mmPos -= 7 * mSpacing;
				mTextYAxisArray[i].mmIndex -= 7;
				if (mTextYAxisArray[i].mmIndex < 0) {
					mTextYAxisArray[i].mmIndex += mNumberArray.length;
				}
			} else if (mTextYAxisArray[i].mmPos <= mStartYPos - mSpacing) {
				mTextYAxisArray[i].mmPos += 7 * mSpacing;
				mTextYAxisArray[i].mmIndex += 7;
				if (mTextYAxisArray[i].mmIndex > mNumberArray.length - 1) {
					mTextYAxisArray[i].mmIndex -= mNumberArray.length;
				}
			}
			
			if (Math.abs(mTextYAxisArray[i].mmPos - mHeight / 2) < mSpacing / 4) {
				mCurrNumPos = mTextYAxisArray[i].mmIndex;
				int oldNumber = mCurrentNumber;
				mCurrentNumber = mNumberArray[mCurrNumPos];
				if (mOnValueChangeListener != null && oldNumber != mCurrentNumber) {
					mOnValueChangeListener.onValueChange(this, oldNumber, mCurrentNumber);
				}
			}
		}
	}
	
	private void fling(int startYVelocity) {
		int maxY = 0;
		if (startYVelocity > 0) {
			maxY = (int) (mTextSizeNormal * (mCurrentNumber - mStartNumber));
			mStartY = 0;
			mFlingScroller.fling(0, 0, 0, startYVelocity, 0, 0, 0, maxY);
		} else if (startYVelocity < 0) {
			maxY = (int) (mTextSizeNormal * (mEndNumber - mCurrentNumber));
			mStartY = maxY;
			mFlingScroller.fling(0, maxY, 0, startYVelocity, 0, 0, 0, maxY);
		}
		invalidate();
	}
	
	class NumberHolder {
		
		public int mmIndex;
		public int mmPos;
		
		public NumberHolder(int index, int position) {
			mmIndex = index;
			mmPos = position;
		}
				
	}
	
	public void setStartNumber(int startNumber) {
		mStartNumber = startNumber;
		verifyNumber();
		initTextYAxisArray();
		//computeYPos(0);
		invalidate();
	}
	
	public void setEndNumber(int endNumber) {
		mEndNumber = endNumber;
		verifyNumber();
		initTextYAxisArray();
		//computeYPos(0);
		invalidate();
	}
	
	public void setCurrentNumber(int currentNumber) {
		mCurrentNumber = currentNumber;
		verifyNumber();
		initTextYAxisArray();
		//computeYPos(0);
		invalidate();
	}
	
	public int getCurrentNumber() {
		return mCurrentNumber;
	}
	
	/**
     * Interface to listen for changes of the current value.
     */
    public interface OnValueChangeListener {

        /**
         * Called upon a change of the current value.
         *
         * @param picker The NumberPicker associated with this listener.
         * @param oldVal The previous value.
         * @param newVal The new value.
         */
        void onValueChange(NumberPicker picker, int oldVal, int newVal);
    }
	
	/**
     * Interface to listen for the picker scroll state.
     */
	public interface OnScrollListener {
		/**
         * The view is not scrolling.
         */
        public static int SCROLL_STATE_IDLE = 0;

        /**
         * The user is scrolling using touch, and their finger is still on the screen.
         */
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;

        /**
         * The user had previously been scrolling using touch and performed a fling.
         */
        public static int SCROLL_STATE_FLING = 2;

        /**
         * Callback invoked while the number picker scroll state has changed.
         *
         * @param view The view whose scroll state is being reported.
         * @param scrollState The current scroll state. One of
         *            {@link #SCROLL_STATE_IDLE},
         *            {@link #SCROLL_STATE_TOUCH_SCROLL} or
         *            {@link #SCROLL_STATE_IDLE}.
         */
        public void onScrollStateChange(NumberPicker picker, int scrollState);
	}
	
	private OnScrollListener mOnScrollListener;
	private OnValueChangeListener mOnValueChangeListener;
	
	public void setOnScrollListener(OnScrollListener l) {
		mOnScrollListener = l;
	}
	
	public void setOnValueChangeListener(OnValueChangeListener l) {
		mOnValueChangeListener = l;
	}
	
	/**
     * Handles transition to a given <code>scrollState</code>
     */
    private void onScrollStateChange(int scrollState) {
        if (mScrollState == scrollState) {
            return;
        }
        mScrollState = scrollState;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChange(this, scrollState);
        }
    }

}
