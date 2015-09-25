package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ZoneAdapter;
import com.zkjinshi.superservice.test.ZoneBiz;
import com.zkjinshi.superservice.view.RefreshLayout;


/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener,RefreshLayout.OnLoadListener {

    private final static String TAG = ZoneActivity.class.getSimpleName();

    private RefreshLayout swipeLayout;
    private ListView zoneLv;
    private ZoneAdapter zoneAdapter;
    private View header;

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
        header = getLayoutInflater().inflate(R.layout.header, null);
        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);


        zoneLv = (ListView)findViewById(R.id.zone_listview);
        zoneAdapter = new ZoneAdapter(this, ZoneBiz.getZoneList());
        zoneLv.setAdapter(zoneAdapter);
    }

    private void initData() {

    }

    private void initListener() {
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);

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

    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据
                // 更新完后调用该方法结束刷新
                swipeLayout.setRefreshing(false);
            }
        }, 1000);

    }

    @Override
    public void onLoad() {
        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据
                // 更新完后调用该方法结束刷新
                swipeLayout.setLoading(false);
            }
        }, 1000);
    }
}
