package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ZoneAdapter;
import com.zkjinshi.superservice.test.ZoneBiz;

/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneActivity extends Activity{

    private final static String TAG = ZoneActivity.class.getSimpleName();
    private ListView zoneLv;
    private ZoneAdapter zoneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        zoneLv = (ListView)findViewById(R.id.zone_listview);
        zoneAdapter = new ZoneAdapter(this, ZoneBiz.getZoneList());
        zoneLv.setAdapter(zoneAdapter);
    }

    private void initData() {

    }

    private void initListener() {
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ZoneActivity.this,MoreActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ZoneActivity.this,MainActivity.class));
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });
    }
}
