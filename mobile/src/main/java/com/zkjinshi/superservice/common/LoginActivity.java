package com.zkjinshi.superservice.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.zkjinshi.superservice.R;

/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginActivity extends Activity implements VerifyPhoneControler.SuccessCallBack{

    private final static String TAG = LoginActivity.class.getSimpleName();

    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        loginBtn = (Button)findViewById(R.id.btn_send);

    }

    private void initData() {
        //VerifyPhoneControler.getInstance().init(this);
        //VerifyPhoneControler.getInstance().setSuccessCallBack(this);

        //测试跳转用的
        loginBtn.setEnabled(true);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,MoreActivity.class));
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });
    }

    private void initListener() {

        findViewById(R.id.shop_register_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ShopRegisterActivity.class));
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });



    }

    @Override
    public void verrifySuccess() {
        Toast.makeText(this,"public void verrifySuccess() ",Toast.LENGTH_LONG).show();
    }
}
