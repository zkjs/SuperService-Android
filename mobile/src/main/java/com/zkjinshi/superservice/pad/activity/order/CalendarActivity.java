package com.zkjinshi.superservice.pad.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.calendarlistview.DatePickerController;
import com.andexert.calendarlistview.DayPickerView;
import com.andexert.calendarlistview.SimpleMonthAdapter;
import com.zkjinshi.superservice.pad.R;
import com.zkjinshi.superservice.pad.base.BaseActivity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 入住和离开日期选择Activity
 * 开发者：杜健德
 * 日期：2015/9/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CalendarActivity extends BaseActivity implements DatePickerController {

    //private ImageButton backIBtn;
    private ImageButton backBtn;//返回
    private DayPickerView dayPickerView;
    private LinearLayout tipsLlyt;
    private TextView tipsTv;
    private boolean isChooseOver;
    private ArrayList<Calendar> calendarList;
    private Calendar checkInCalendar,checkOutCalendar;

    public static final int CALENDAR_REQUEST_CODE = 5;

    private void initView(){

        backBtn = (ImageButton)findViewById(R.id.back_btn);
        dayPickerView = (DayPickerView)findViewById(R.id.date_picker_choose_view);
        tipsLlyt = (LinearLayout)findViewById(R.id.calendar_tips_llyt);
        tipsTv = (TextView)findViewById(R.id.calendar_tips_tv);
    }

    private void initData(){
        dayPickerView.setController(this);

        Intent data = getIntent();
        Serializable serializable =  data.getSerializableExtra("calendarList");
        if(serializable != null)
        {
            calendarList =  (ArrayList<Calendar>)serializable;
            SimpleMonthAdapter adapter = (SimpleMonthAdapter)dayPickerView.getAdapter();
            adapter.setFirstDayAndLastDay(calendarList.get(0), calendarList.get(1));
            adapter.notifyDataSetChanged();

            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            int selectMonth = calendarList.get(0).get(Calendar.MONTH);
            dayPickerView.setSelection(selectMonth - currentMonth);
        }
    }

    private void initListeners(){

        //返回
       backBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               setResult(RESULT_CANCELED);
               finish();
               overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
           }
       });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initView();
        initData();
        initListeners();
    }

    @Override
    public int getMaxYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {

    }

    public void onFirstDaySelected(int year, int month, int day) {
        calendarList = null;
        calendarList = new ArrayList<Calendar>();
        checkInCalendar = Calendar.getInstance();
        checkInCalendar.set(Calendar.YEAR,year);
        checkInCalendar.set(Calendar.MONTH,month);
        checkInCalendar.set(Calendar.DAY_OF_MONTH,day);
        calendarList.add(checkInCalendar);

        showSelectLastDayTips();
    }
    private void showSelectLastDayTips() {
        Animation scaleAnimation1 = new ScaleAnimation(1.0f, 0.0f,1.0f,0.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation1.setDuration(200);
        scaleAnimation1.setFillAfter(true);
        scaleAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tipsTv.setText("请选择离店日期");
                Animation scaleAnimation2 = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation2.setDuration(200);
                scaleAnimation2.setFillAfter(true);
                tipsLlyt.startAnimation(scaleAnimation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tipsLlyt.startAnimation(scaleAnimation1);
    }

    public void onLastDaySelected(int year, int month, int day) {
        checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.set(Calendar.YEAR, year);
        checkOutCalendar.set(Calendar.MONTH, month);
        checkOutCalendar.set(Calendar.DAY_OF_MONTH, day);
        calendarList.add(checkOutCalendar);
        //跳转回选择页面
        Intent inetnt = new Intent();
        inetnt.putExtra("calendarList", calendarList);
        setResult(RESULT_OK, inetnt);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
