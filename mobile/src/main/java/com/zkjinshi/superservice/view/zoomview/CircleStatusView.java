package com.zkjinshi.superservice.view.zoomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.zkjinshi.superservice.R;

/**
 * 圆形状态控件
 * 开发者：dujiande
 * 日期：2015/10/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CircleStatusView extends View{

    enum CircleStatus{
        STATUS_LOADING,
        STATUS_FINISH,
        STATUS_MORE
    }

    private CircleStatus status = CircleStatus.STATUS_LOADING;
    private int bgcolor =  Color.GREEN;
    protected Paint mSelectedCirclePaint;
    private int width;
    private int height;

    public CircleStatus getStatus() {
        return status;
    }

    public void setStatus(CircleStatus status) {
        this.status = status;
    }

    public int getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(int bgcolor) {
        this.bgcolor = bgcolor;
    }

    public CircleStatusView(Context context) {
        super(context);
        initView();
    }

    public CircleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CircleStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    protected  void onMeasure(int width,int height){
        super.onMeasure(width,height);
        this.height = getMeasuredHeight();
        this.width = getMeasuredWidth();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int radius = width/2;

        mSelectedCirclePaint.setColor(bgcolor);
        canvas.drawCircle(width/2, height/2, radius, mSelectedCirclePaint);

        //画图片，就是贴图
        // 创建画笔
        Bitmap bitmap = null;
        if(status ==  CircleStatus.STATUS_LOADING){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_hourglass);
        }else if(status ==  CircleStatus.STATUS_FINISH){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_right);
        }
        else if(status ==  CircleStatus.STATUS_FINISH){
           bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_more);
        }
        int h = bitmap.getHeight();
        int w = bitmap.getWidth();
        canvas.drawBitmap(bitmap,radius-w/2,radius-h/2,mSelectedCirclePaint);

    }
}
