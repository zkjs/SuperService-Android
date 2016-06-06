package com.zkjinshi.superservice.pad.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zkjinshi.base.config.ConfigActivity;
import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.pad.base.BaseActivity;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.SensorManagerHelper;
import com.zkjinshi.superservice.pad.R;

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
    private RelativeLayout contentLlt;
    private ScrollView scrollView;
    private float offsetY;

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
        contentLlt = (RelativeLayout) findViewById(R.id.content_layout_rl);
        scrollView = (ScrollView)findViewById(R.id.content_body_sv);
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
        offsetY =  getResources().getDimension(R.dimen.key_up_login_height);
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

        scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > 0)){
                    moveUp();
                }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > 0)){
                    moveDown();
                }
            }
        });
    }

    private void moveUp(){
        long time = 300;
        ViewHelper.setTranslationY(contentLlt,0);
        ViewPropertyAnimator.animate(contentLlt).translationYBy(-offsetY).setDuration(time);
    }

    private void moveDown(){
        long time = 300;
        ViewHelper.setTranslationY(contentLlt,-offsetY);
        ViewPropertyAnimator.animate(contentLlt).translationYBy(offsetY).setDuration(time);
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
