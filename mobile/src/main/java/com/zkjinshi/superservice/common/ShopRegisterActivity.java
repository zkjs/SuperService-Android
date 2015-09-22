package com.zkjinshi.superservice.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.zkjinshi.superservice.R;

/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopRegisterActivity extends Activity{

    private final static String TAG = ShopRegisterActivity.class.getSimpleName();
    private Button registerBtn;

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
        registerBtn  = (Button)findViewById(R.id.btn_send);
    }

    private void initData() {

    }

    private void initListener() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopRegisterActivity.this,ShopMoreActivity.class));
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });
    }
}
