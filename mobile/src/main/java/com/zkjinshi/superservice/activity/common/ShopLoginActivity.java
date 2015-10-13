package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.zkjinshi.superservice.R;

/**
 * 商家登录
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopLoginActivity extends Activity{

    private final static String TAG = ShopLoginActivity.class.getSimpleName();

    private EditText nameEt;
    private EditText pwdEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        nameEt = (EditText)findViewById(R.id.et_input_phone);
        pwdEt = (EditText)findViewById(R.id.et_password);
    }

    private void initData() {


    }

    private void initListener() {
        findViewById(R.id.tv_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopLoginActivity.this, ShopRegisterActivity.class));
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginRequest();

            }
        });
    }

    private void loginRequest(){
        startActivity(new Intent(ShopLoginActivity.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }


}
