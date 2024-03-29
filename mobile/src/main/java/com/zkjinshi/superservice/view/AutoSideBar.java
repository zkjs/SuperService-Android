package com.zkjinshi.superservice.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.superservice.R;

/**
 * 右侧字母选择
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class AutoSideBar extends View {

	// 触摸事件
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

	// 右侧选择字母
	public  String[] sortLettArray;
	private int   choose  = -1;// 选中
	private Paint  paint  = new Paint();

	private TextView mTextDialog;

	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}

	public AutoSideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AutoSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AutoSideBar(Context context) {
		super(context);
	}

	public void setSortArray(String[] letterArray){
		this.sortLettArray = letterArray;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 获取焦点改变背景颜色.
		int height = getHeight();// 获取对应高度
		int width  = getWidth(); // 获取对应宽度

		float singleHeight = (height * 1f) / sortLettArray.length;// 获取每一个字母的高度
        singleHeight = (height * 1f - singleHeight/2) / sortLettArray.length;
		for (int i = 0; i < sortLettArray.length; i++) {
			//设置字体显示颜色
			paint.setColor(Color.GRAY);
			// paint.setColor(Color.WHITE);
			// paint.setTypeface(Typeface.DEFAULT_BOLD);// 去除加粗
			paint.setAntiAlias(true);
			paint.setTextSize(DisplayUtil.sp2px(getContext(), 16));
			// 选中的状态
			if (i == choose) {
				paint.setColor(Color.WHITE);
				paint.setFakeBoldText(true);
			}

			if(this.sortLettArray[0].equals("?")){
				//确定圆形
				float xPoint = width / 2;
				float yPoint = singleHeight / 2;
				float radius = xPoint / 2;
				//开始画圆形
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(radius * 0.1f);
				canvas.drawCircle(xPoint, yPoint, radius, paint);
				canvas.drawLine(xPoint, yPoint, xPoint +  (radius * 0.8f), yPoint, paint);
				canvas.drawLine(xPoint, yPoint, xPoint, yPoint - (radius * 0.7f), paint);
			}else {
                // x坐标等于中间-字符串宽度的一半.
                float xPos = width / 2 - paint.measureText(sortLettArray[i]) / 2;
                float yPos = singleHeight * i + singleHeight;
                canvas.drawText(sortLettArray[i], xPos, yPos, paint);
            }
			paint.reset();// 重置画笔
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();// 点击y坐标
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * sortLettArray.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

		switch (action) {
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose = -1;//
			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}
			break;

		default:
			setBackgroundResource(R.drawable.sidebar_background);
			if (oldChoose != c) {
				if (c >= 0 && c < sortLettArray.length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(sortLettArray[c]);
					}
					if (mTextDialog != null) {
						//显示最近联系人
						mTextDialog.setText(sortLettArray[c]);
						mTextDialog.setVisibility(View.VISIBLE);
					}
					choose = c;
					invalidate();
				}
			}
			break;
		}
		return true;
	}

	/**
	 * 向外公开的方法
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	/** 接口 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}