package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.zkjinshi.superservice.R;

import org.apache.log4j.chainsaw.Main;

/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopRegisterActivity extends Activity  implements VerifyPhoneControler.SuccessCallBack{

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VerifyPhoneControler.getInstance().unregisterSmsReceiver();
    }

    private void initView() {
        registerBtn  = (Button)findViewById(R.id.btn_send);
    }

    private void initData() {
        VerifyPhoneControler.getInstance().init(this);
        VerifyPhoneControler.getInstance().registerSmsReceiver();
        VerifyPhoneControler.getInstance().setSuccessCallBack(this);

        //测试跳转用的
        registerBtn.setEnabled(true);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopRegisterActivity.this,MainActivity.class));
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });
    }

    private void initListener() {

    }

    @Override
    public void verrifySuccess() {

    }
}
