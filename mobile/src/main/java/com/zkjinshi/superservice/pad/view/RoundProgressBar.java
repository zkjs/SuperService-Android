package com.zkjinshi.superservice.pad.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.pad.R;


/**
 * 自定义圆环进度条
 * 开发者：JimmyZhang
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class RoundProgressBar extends View {

	//Color
	public static final int DEFAULT_ROUND_COLOR = Color.parseColor("#2E666666");
	public static final int DEFAULT_ROUND_PROGRESS_COLOR = Color.parseColor("#FFFF0000");
	public static final int DEFAULT_VALUE_TEXT_COLOR = Color.parseColor("#FFFF0000");

	//Number
	public static final int DEFAULT_CIRCLE_STROKE_WIDTH = 4;
	public static final int DEFAULT_VALUE_TEXT_SIZE = 25;
	public static final int DEFAULT_ANIM_DURATION = 1000 * 2;

	//Style
	public static final int STYLE_STROKE = 0;
	public static final int STYLE_FILL = 1;

	//boolean
	public static final boolean DEFAULT_VALUE_TEXT_IS_DISPLAYABLE = true;

	//String
	public static final String DEFAULT_VALUE_TEXT = "";


	private int mMeasureHeight;
	private int mMeasureWidth;

	private Paint valuePaint;
	private String valueText;
	private int valueTextColor;
	private int valueTextSize;

	private int despTextColor;
	private int despTextSize;

	private Paint mCirclePaint;
	private float mCircleXY;
	private float mRadius;

	private Paint mArcPaint;
	private RectF mArcRectF;
	private float sweepValue = 0;
	private float sweepAngle;
	private int circleStrokeWidth;

	private int roundColor;
	private int roundProgressColor;
	private float max = 100;
	private boolean valueTextIsDisplayable;
	private int circleStyle;

	private ValueAnimator mValueAnimator;
	private Interpolator interpolator  = new BounceInterpolator();
	private int animDuration = DEFAULT_ANIM_DURATION;

	public RoundProgressBar(Context context) {
		super(context);
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
		roundColor = typedArray.getColor(R.styleable.CircleProgress_roundColor, DEFAULT_ROUND_COLOR);
		roundProgressColor = typedArray.getColor(R.styleable.CircleProgress_roundProgressColor, DEFAULT_ROUND_PROGRESS_COLOR);
		circleStrokeWidth = (int) typedArray.getDimension(R.styleable.CircleProgress_circleStrokeWidth, DEFAULT_CIRCLE_STROKE_WIDTH);
		valueText = typedArray.getString(R.styleable.CircleProgress_valueText);
		valueTextColor = typedArray.getColor(R.styleable.CircleProgress_valueTextColor, DEFAULT_VALUE_TEXT_COLOR);
		valueTextSize = (int) typedArray.getDimension(R.styleable.CircleProgress_valueTextSize, DEFAULT_VALUE_TEXT_SIZE);
		if (valueText == null) {
			valueText = DEFAULT_VALUE_TEXT;
		}
		valueTextIsDisplayable = typedArray.getBoolean(R.styleable.CircleProgress_valueTextIsDisplayable, DEFAULT_VALUE_TEXT_IS_DISPLAYABLE);
		circleStyle = typedArray.getInt(R.styleable.CircleProgress_roundStyle, STYLE_STROKE);
		typedArray.recycle();

	}

	public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mMeasureWidth = MeasureSpec.getSize(widthMeasureSpec);
		mMeasureHeight = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(mMeasureWidth, mMeasureHeight);

		init();
	}

	private void init() {
		float length;
		if (mMeasureHeight >= getMeasuredWidth()) {
			length = mMeasureWidth;
		} else {
			length = mMeasureHeight;
		}

		mCircleXY = length / 2;
		mRadius = length * 18 / 40 - 2;

		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setColor(roundColor);
		mCirclePaint.setStrokeWidth(circleStrokeWidth);

		mArcRectF = new RectF(
				mCircleXY - mRadius,
				mCircleXY - mRadius,
				mCircleXY + mRadius,
				mCircleXY + mRadius);
		sweepAngle = value2Angle(sweepValue);
		mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mArcPaint.setColor(roundProgressColor);
		mArcPaint.setStrokeWidth(circleStrokeWidth);

		//设置空心或实心
		if (circleStyle == STYLE_STROKE) {
			mArcPaint.setStyle(Paint.Style.STROKE);
			mCirclePaint.setStyle(Paint.Style.STROKE);
		} else {
			mArcPaint.setStyle(Paint.Style.FILL);
			mCirclePaint.setStyle(Paint.Style.FILL);
		}

		valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		valuePaint.setColor(valueTextColor);
		valuePaint.setTextSize(valueTextSize);
		valuePaint.setTextAlign(Paint.Align.CENTER);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawCircle(mCircleXY, mCircleXY, mRadius, mCirclePaint);

		if (circleStyle == STYLE_STROKE) {
			canvas.drawArc(mArcRectF, 0, sweepAngle, false, mArcPaint);
		} else {
			canvas.drawArc(mArcRectF, 0, sweepAngle, true, mArcPaint);
		}

		if (valueTextIsDisplayable) {
			/*canvas.drawText(valueText, 0, valueText.length(),
					mCircleXY, mCircleXY - (valueTextSize / 5), valuePaint);*/
			/*canvas.drawText(valueText, 0, valueText.length(),
					mCircleXY, mCircleXY - (valueTextSize), valuePaint);
			canvas.drawText(valueText, 0, valueText.length(),
					mCircleXY, mCircleXY + (valueTextSize), valuePaint);*/

			if(!TextUtils.isEmpty(valueText) && valueText.length() <= 5){//文字小于5个单行显示
				Paint.FontMetricsInt fontMetrics = valuePaint.getFontMetricsInt();
				int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
				canvas.drawText(valueText,mCircleXY, baseline, valuePaint);
			}else {//两行显示文本
				/*String firstLineStr = valueText.substring(0,2);
				String secondLineStr = valueText.substring(2,valueText.length());
				Paint.FontMetricsInt fontMetrics = valuePaint.getFontMetricsInt();
				int firstBaseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top/2-DisplayUtil.dip2px(getContext(),3) ;
				int secondBaseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top*3/2+DisplayUtil.dip2px(getContext(),3);
				canvas.drawText(firstLineStr,mCircleXY, firstBaseline, valuePaint);
				canvas.drawText(secondLineStr,mCircleXY, secondBaseline, valuePaint);*/
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		anim();
	}

	public void anim() {
		mValueAnimator = ValueAnimator.ofFloat(0, sweepValue);
		mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator animation) {
				setSweepValue((Float) animation.getAnimatedValue());
			}
		});
		mValueAnimator.setInterpolator(interpolator);
		mValueAnimator.setDuration(animDuration);
		mValueAnimator.start();
	}

	public void setValueText(String value) {
		valueText = value;
		forceInvalidate();
	}

	public void setValueTextColor(int color) {
		valueTextColor = color;
		forceInvalidate();
	}

	public void setValueTextSize(int size) {
		valueTextSize = size;
		forceInvalidate();
	}

	public void setValueTextIsDisplayable(boolean valueTextIsDisplayable) {
		this.valueTextIsDisplayable = valueTextIsDisplayable;
		forceInvalidate();
	}

	public void setRoundColor(int roundColor) {
		this.roundColor = roundColor;
		forceInvalidate();
	}

	public void setRoundProgressColor(int roundProgressColor) {
		this.roundProgressColor = roundProgressColor;
		forceInvalidate();
	}


	public void setCircleStrokeWidth(int width) {
		mCirclePaint.setStrokeWidth(width);
		mArcPaint.setStrokeWidth(width);
		forceInvalidate();
	}

	public void setCircleStyle(int circleStyle) {
		this.circleStyle = circleStyle;
		forceInvalidate();
	}

	public void setSweepAngle(float sweepAngle) {
		this.sweepAngle = sweepAngle;
		forceInvalidate();
	}

	public void setInterpolator(Interpolator interpolator) {
		this.interpolator = interpolator;
	}

	public void setAnimDuration(int animDuration) {
		this.animDuration = animDuration;
	}

	public void forceInvalidate() {
		this.invalidate();
	}

	public void setSweepValue(float sweepValue) {
		if (sweepValue > 0) {
			this.sweepValue = sweepValue;
		}else if(sweepValue > 100){
			this.sweepValue = 100;
		}
		sweepAngle = value2Angle(sweepValue);
		this.invalidate();
	}

	private float value2Angle(float value) {
		return (value / max * 360f);
	}

}
