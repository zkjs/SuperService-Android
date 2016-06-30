package com.zkjinshi.superservice.activity.set;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.base.BaseAppCompatActivity;

import java.util.Date;

/**
 * 创建活动
 * 开发者：jimmyzhang
 * 日期：16/6/28
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class EditEventActivity extends BaseAppCompatActivity {

    private Toolbar toolbar;
    private TextView titleIv;
    private Button sureBtn;
    private EditText eventNameEtv,eventContentEtv,carryCountEtv,eventUrlEtv;
    private TextView startDateTv,overDateTv,cutLineTv;
    private SimpleDraweeView eventLogoIv;
    private CheckBox isCarryCb;//是否携带人
    private RelativeLayout carryInputLayout;
    private ScrollView contentSv;
    private TimePickerView timePickerView;
    private boolean isEventStartDate = true;

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleIv = (TextView) findViewById(R.id.tv_center_title);
        sureBtn = (Button)findViewById(R.id.create_event_btn_sure);
        eventNameEtv = (EditText)findViewById(R.id.create_event_etv_event_name);
        startDateTv = (TextView) findViewById(R.id.create_event_tv_event_start_date);
        overDateTv = (TextView)findViewById(R.id.create_event_tv_event_over_date);
        eventContentEtv = (EditText)findViewById(R.id.create_event_etv_event_content);
        carryCountEtv = (EditText)findViewById(R.id.create_event_etv_carry_count);
        eventUrlEtv = (EditText)findViewById(R.id.create_event_tv_event_url);
        eventLogoIv = (SimpleDraweeView)findViewById(R.id.create_event_sdv_event_logo);
        isCarryCb = (CheckBox)findViewById(R.id.create_event_cb_carry);
        cutLineTv = (TextView)findViewById(R.id.create_event_tv_cut_line);
        carryInputLayout = (RelativeLayout)findViewById(R.id.create_event_tv_carry_input_layout);
        contentSv = (ScrollView)findViewById(R.id.create_event_sv_content);
    }

    private void initData(){
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleIv.setText("创建活动");
        timePickerView = new TimePickerView(this, TimePickerView.Type.MONTH_DAY_HOUR_MIN);
        timePickerView.setTime(new Date());
        timePickerView.setCyclic(false);
        timePickerView.setCancelable(true);

    }

    private void initListeners(){

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        /**
         * 是否携带他人
         */
        isCarryCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    cutLineTv.setVisibility(View.VISIBLE);
                    carryInputLayout.setVisibility(View.VISIBLE);
                }else {
                    cutLineTv.setVisibility(View.GONE);
                    carryInputLayout.setVisibility(View.GONE);
                }
            }
        });

        //时间选择后回调
        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                if(isEventStartDate){
                    startDateTv.setText(TimeUtil.getTime(date.getTime()));
                }else {
                    overDateTv.setText(TimeUtil.getTime(date.getTime()));
                }
            }
        });

        // 开始时间
        startDateTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isEventStartDate = true;
                timePickerView.show();
            }
        });

        // 结束时间
        overDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEventStartDate = false;
                timePickerView.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        initView();
        initData();
        initListeners();
    }



}
