package com.zkjinshi.superservice.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zkjinshi.superservice.R;

import java.util.Random;

/**
 * 圆形状态TextView
 * 开发者：dujiande
 * 日期：2015/10/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CircleTextView extends TextView{

    private int bgcolor =   Color.rgb(new Random().nextInt(250), new Random().nextInt(250), new Random().nextInt(250));

    protected Paint mSelectedCirclePaint;
    private int width;
    private int height;

    public int getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(int bgcolor) {
        this.bgcolor = bgcolor;
    }

    public CircleTextView(Context context) {
        super(context);
        initView();
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setFakeBoldText(true);
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setColor(bgcolor);
        mSelectedCirclePaint.setTextAlign(Paint.Align.CENTER);
        mSelectedCirclePaint.setStyle(Paint.Style.FILL);
        mSelectedCirclePaint.setAlpha(50);
    }

    public void setText(String text){
        super.setText(text,BufferType.NORMAL);
        invalidate();
    }

    protected  void onMeasure(int width,int height){
        super.onMeasure(width,height);
        this.height = getMeasuredHeight();
        this.width = getMeasuredWidth();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int radius = width/2;

        mSelectedCirclePaint.setAlpha(128);
        mSelectedCirclePaint.setColor(bgcolor);
        canvas.drawCircle(width / 2, height / 2, radius, mSelectedCirclePaint);

    }
}
