package com.zkjinshi.superservice.pad.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;

import android.widget.EditText;

import com.zkjinshi.base.config.ConfigActivity;
import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.pad.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.utils.SensorManagerHelper;
import com.zkjinshi.superservice.pad.vo.IdentityType;
import com.zkjinshi.superservice.pad.vo.PayloadVo;
import com.zkjinshi.superservice.pad.R;

import com.zkjinshi.superservice.pad.base.BaseActivity;
import com.zkjinshi.superservice.pad.manager.SSOManager;

import com.zkjinshi.superservice.pad.utils.MD5Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 商家登录
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopLoginActivity extends BaseActivity{

    private final static String TAG = ShopLoginActivity.class.getSimpleName();

    private EditText nameEt;
    private EditText pwdEt;
    private SensorManagerHelper sensorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

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
        nameEt = (EditText)findViewById(R.id.et_input_phone);
        pwdEt = (EditText)findViewById(R.id.et_password);
    }

    private void initData() {
        //打开配置项
        sensorHelper = new SensorManagerHelper(this);
        sensorHelper.setOnShakeListener(new SensorManagerHelper.OnShakeListener() {

            @Override
            public void onShake() {
                if(!ActivityManagerHelper.isRunningBackground(ShopLoginActivity.this)) {
                    Intent intent = new Intent(ShopLoginActivity.this, ConfigActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                }
            }
        });
        LoginController.getInstance().init(this);
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

        findViewById(R.id.sales_login_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopLoginActivity.this, LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
    }

    private void loginRequest(){
        final String name =  nameEt.getText().toString();
        final String password = pwdEt.getText().toString();
        if(TextUtils.isEmpty(name)){
            DialogUtil.getInstance().showToast(this,"用户号不能为空");
            return;
        }else if(TextUtils.isEmpty(password)){
            DialogUtil.getInstance().showToast(this,"密码不能为空");
            return;
        }
        LoginController.getInstance().getTokenByPassword(this, name, MD5Util.MD5(password), new LoginController.CallBackListener() {
            @Override
            public void successCallback(JSONObject response) {
                String token = null;
                try {
                    token = response.getString("token");
                    CacheUtil.getInstance().setExtToken(token);
                    PayloadVo payloadVo = SSOManager.getInstance().decodeToken(token);
                    CacheUtil.getInstance().setUserId(payloadVo.getSub());
                    CacheUtil.getInstance().setLoginIdentity(IdentityType.BUSINESS);
                    DBOpenHelper.DB_NAME = payloadVo.getSub() + ".db";
                    getUserInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取详细信息
     */
    private void getUserInfo() {
        LoginController.getInstance().getUserInfo(this, CacheUtil.getInstance().getUserId(), new LoginController.CallBackListener() {
            @Override
            public void successCallback(JSONObject response) {
                try{
                    Intent mainIntent = new Intent(ShopLoginActivity.this, ZoneActivity.class);
                    startActivity(mainIntent);
                    finish();
                    overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }


}
