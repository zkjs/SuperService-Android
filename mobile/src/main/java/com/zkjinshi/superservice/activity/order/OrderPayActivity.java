package com.zkjinshi.superservice.activity.order;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.zkjinshi.superservice.R;


/**
 * 账单确定
 * 开发者：dujiande
 * 日期：2015/10/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderPayActivity  extends Activity {
    private final static String TAG = OrderPayActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pay);

        initView();
        initData();
        initListener();
    }

    private void initView() {

    }

    private void initData() {

    }

    private void initListener() {
        findViewById(R.id.back_btn_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });

        findViewById(R.id.go_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
