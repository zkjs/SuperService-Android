package com.zkjinshi.superservice.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.zkjinshi.superservice.R;

/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopMoreActivity extends Activity {

    private final static String TAG = ShopMoreActivity.class.getSimpleName();
    private Spinner spinner;

    private static final String[] mShopType ={"五星级酒店","四星级酒店","三星级酒店","商务型酒店","舒适型酒店","普通酒店"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_more);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        spinner = (Spinner)findViewById(R.id.spinner);

    }

    private void initData() {
        ArrayAdapter<String> ad= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mShopType);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(ad);
    }

    private void initListener() {
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopMoreActivity.this,ShopRegisterActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(ShopMoreActivity.this,ZoneActivity.class));
//                finish();
//                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });
    }
}
