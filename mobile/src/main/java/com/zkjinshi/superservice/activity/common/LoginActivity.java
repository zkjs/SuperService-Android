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
import com.zkjinshi.superservice.bean.SempLoginBean;
import com.zkjinshi.superservice.factory.UserFactory;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.UserVo;

import java.util.HashMap;

/**
 * 开发者：dujiande
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginActivity extends Activity implements VerifyPhoneControler.SuccessCallBack{

    private final static String TAG = LoginActivity.class.getSimpleName();

    private Button loginBtn;
    private EditText inputEt;


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
        inputEt = (EditText)findViewById(R.id.et_input_phone);
    }

    private void initData() {
        LoginController.getInstance().init(this);
        //VerifyPhoneControler.getInstance().init(this);
        //VerifyPhoneControler.getInstance().setSuccessCallBack(this);

        //测试跳转用的
        inputEt.setText("18912345678");//18912345678
        loginBtn.setEnabled(true);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = inputEt.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    DialogUtil.getInstance().showToast(LoginActivity.this, "电话号码不能为空");
                    return;
                }
                LoginController.getInstance().requestLogin(phone);
            }
        });
    }

    private void initListener() {

        findViewById(R.id.shop_register_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ShopRegisterActivity.class));
                finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });

    }

    @Override
    public void verrifySuccess() {
        String phone = inputEt.getText().toString();
        if(TextUtils.isEmpty(phone)){
            DialogUtil.getInstance().showToast(this,"电话号码不能为空");
            return;
        }
        LoginController.getInstance().requestLogin(phone);
    }


}
