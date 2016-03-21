package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.set.ClientController;
import com.zkjinshi.superservice.activity.set.TeamContactsController;
import com.zkjinshi.superservice.base.BaseActivity;
import com.zkjinshi.superservice.bean.SempLoginBean;
import com.zkjinshi.superservice.factory.UserFactory;
import com.zkjinshi.superservice.manager.YunBaSubscribeManager;
import com.zkjinshi.superservice.manager.ZoneManager;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.notification.NotificationHelper;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.UserVo;

import org.json.JSONObject;

/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginActivity extends BaseActivity implements VerifyPhoneControler.SuccessCallBack{

    private final static String TAG = LoginActivity.class.getSimpleName();

    private Button   loginBtn;
    private EditText inputEt;

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
        VerifyPhoneControler.getInstance().unregisterSmsReceiver();
    }

    private void initView() {
        loginBtn = (Button)findViewById(R.id.btn_send);
        inputEt = (EditText)findViewById(R.id.et_input_phone);
    }

    private void initData() {
        LoginController.getInstance().init(this);
        VerifyPhoneControler.getInstance().init(this);
        VerifyPhoneControler.getInstance().registerSmsReceiver();
        VerifyPhoneControler.getInstance().setSuccessCallBack(this);

        //测试跳转用的
//        inputEt.setText("");
//        loginBtn.setEnabled(true);
//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                verrifySuccess();
//            }
//        });
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
                intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });
    }
}
