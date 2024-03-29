package com.zkjinshi.superservice.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.zkjinshi.base.config.ConfigActivity;
import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.SensorManagerHelper;

import org.json.JSONObject;

/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginActivity extends BaseActivity implements VerifyPhoneControler.SuccessCallBack{

    private EditText inputEt;
    private SensorManagerHelper sensorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != sensorHelper){
            sensorHelper.stop();
        }
    }

    private void initView() {
        inputEt = (EditText)findViewById(R.id.et_input_phone);
    }

    private void initData() {
        //打开配置项
        sensorHelper = new SensorManagerHelper(this);
        sensorHelper.setOnShakeListener(new SensorManagerHelper.OnShakeListener() {

            @Override
            public void onShake() {
                if(!ActivityManagerHelper.isRunningBackground(LoginActivity.this)) {
                    Intent intent = new Intent(LoginActivity.this, ConfigActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                }
            }
        });
        LoginController.getInstance().init(this);
        VerifyPhoneControler.getInstance().init(this);
        VerifyPhoneControler.getInstance().setSuccessCallBack(this);
    }

    private void initListener() {

        findViewById(R.id.shop_register_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ShopLoginActivity.class));
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });

    }

    @Override
    public void verrifySuccess() {
        final String phone = inputEt.getText().toString();
        if(TextUtils.isEmpty(phone)){
            DialogUtil.getInstance().showToast(this,"电话号码不能为空");
            return;
        }
        LoginController.getInstance().getUserInfo(this, CacheUtil.getInstance().getUserId(), new LoginController.CallBackListener() {
            @Override
            public void successCallback(JSONObject response) {
                CacheUtil.getInstance().setLogin(true);
                Intent intent;
                String ZONE_CACHE_KEY = "zoneBeanList"+CacheUtil.getInstance().getUserId();
                String listStr = CacheUtil.getInstance().getListStrCache(ZONE_CACHE_KEY);
                if(!TextUtils.isEmpty(listStr)){
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                }else{
                    intent = new Intent(LoginActivity.this,ZoneActivity.class);
                }
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);

            }
        });
    }
}
