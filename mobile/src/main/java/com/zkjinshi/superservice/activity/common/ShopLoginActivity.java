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
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.notice.LocNoticeController;
import com.zkjinshi.superservice.activity.set.TeamContactsController;
import com.zkjinshi.superservice.bean.AdminLoginBean;
import com.zkjinshi.superservice.factory.UserFactory;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.MD5Util;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.UserVo;

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
    }

    private void loginRequest(){
        final String phone =  nameEt.getText().toString();
        final String password = pwdEt.getText().toString();
        if(TextUtils.isEmpty(phone)){
            DialogUtil.getInstance().showToast(this,"手机号不能为空");
            return;
        }else if(TextUtils.isEmpty(password)){
            DialogUtil.getInstance().showToast(this,"密码不能为空");
            return;
        }
        LogUtil.getInstance().info(LogLevel.INFO,"管理员开始登陆。。。");
        LoginController.getInstance().requestAdminLogin(phone, MD5Util.MD5(password),true,new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                if(null != result && !TextUtils.isEmpty(result.rawResult)){
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    AdminLoginBean adminLoginBean = new Gson().fromJson(result.rawResult, AdminLoginBean.class);
                    if (adminLoginBean.isSet()) {
                        CacheUtil.getInstance().setToken(adminLoginBean.getToken());
                        CacheUtil.getInstance().setUserId(adminLoginBean.getUserid());
                        CacheUtil.getInstance().setUserPhone(phone);
                        CacheUtil.getInstance().setUserName(adminLoginBean.getName());
                        CacheUtil.getInstance().setShopID(adminLoginBean.getShopid());
                        CacheUtil.getInstance().setShopFullName(adminLoginBean.getFullname());
                        CacheUtil.getInstance().setLoginIdentity(IdentityType.BUSINESS);
                        CacheUtil.getInstance().setPassword(password);
                        CacheUtil.getInstance().setAreaInfo(adminLoginBean.getLocid());
                        String userID = CacheUtil.getInstance().getUserId();
                        String token  = CacheUtil.getInstance().getToken();
                        String shopiD = CacheUtil.getInstance().getShopID();
                        DBOpenHelper.DB_NAME = adminLoginBean.getUserid() + ".db";
                        LoginController.getInstance().getDeptList(userID, token, shopiD);//获取部门列表
                        TeamContactsController.getInstance().getTeamContacts(ShopLoginActivity.this, userID, token, shopiD, null);//获取团队列表
                        LocNoticeController.getInstance().init(ShopLoginActivity.this).requestLocTask();//获取区域信息
                        UserVo userVo = UserFactory.getInstance().buildUserVo(adminLoginBean);
                        UserDBUtil.getInstance().addUser(userVo);
                        String avatarUrl = ProtocolUtil.getShopLogoUrl(adminLoginBean.getShopid());
                        CacheUtil.getInstance().saveUserPhotoUrl(avatarUrl);
                       // Intent mainIntent = new Intent(ShopLoginActivity.this, MainActivity.class);
                        Intent mainIntent = new Intent(ShopLoginActivity.this, ZoneActivity.class);
                        startActivity(mainIntent);
                        finish();
                        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                        LogUtil.getInstance().info(LogLevel.INFO, "管理员成功登陆。。。");
                    } else {
                        DialogUtil.getInstance().showToast(ShopLoginActivity.this, "密码或者手机号不对 ");
                    }
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
    }


}
