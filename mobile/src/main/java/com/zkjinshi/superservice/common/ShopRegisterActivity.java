package com.zkjinshi.superservice.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.zkjinshi.superservice.R;

/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopRegisterActivity extends Activity{

    private final static String TAG = ShopRegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_register);

        initView();
        initData();
        initListener();
    }

    private void initView() {

    }

    private void initData() {

    }

    private void initListener() {

    }
}
