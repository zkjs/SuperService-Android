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
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.IdentityType;
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
        VerifyPhoneControler.getInstance().init(this);
        VerifyPhoneControler.getInstance().setSuccessCallBack(this);

        //测试跳转用的
//        inputEt.setText("14000800924");//18912345678
//        loginBtn.setEnabled(true);
//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final String phone = inputEt.getText().toString();
//                if (TextUtils.isEmpty(phone)) {
//                    DialogUtil.getInstance().showToast(LoginActivity.this, "电话号码不能为空");
//                    return;
//                }
//                LoginController.getInstance().requestLogin(phone,true,new NetRequestListener() {
//                    @Override
//                    public void onNetworkRequestError(int errorCode, String errorMessage) {
//                        Log.i(TAG, "errorCode:" + errorCode);
//                        Log.i(TAG, "errorMessage:" + errorMessage);
//                    }
//
//                    @Override
//                    public void onNetworkRequestCancelled() {
//
//                    }
//
//                    @Override
//                    public void onNetworkResponseSucceed(NetResponse result) {
//                        Log.i(TAG, "result.rawResult:" + result.rawResult);
//                        SempLoginBean sempLoginbean = new Gson().fromJson(result.rawResult, SempLoginBean.class);
//                        if (sempLoginbean.isSet()) {
//                            //更新为最新的token和userid
//                            CacheUtil.getInstance().setToken(sempLoginbean.getToken());
//                            CacheUtil.getInstance().setUserId(sempLoginbean.getSalesid());
//                            CacheUtil.getInstance().setUserPhone(phone);
//                            CacheUtil.getInstance().setUserName(sempLoginbean.getName());
//                            CacheUtil.getInstance().setShopID(sempLoginbean.getShopid());
//                            CacheUtil.getInstance().setShopFullName(sempLoginbean.getFullname());
//                            CacheUtil.getInstance().setLoginIdentity(IdentityType.WAITER);
//                            String userID = CacheUtil.getInstance().getUserId();
//                            String token  = CacheUtil.getInstance().getToken();
//                            String shopiD = CacheUtil.getInstance().getShopID();
//                            DBOpenHelper.DB_NAME = sempLoginbean.getSalesid() + ".db";
//                            LoginController.getInstance().getDeptList(userID, token, shopiD);//获取部门列表
//                            TeamContactsController.getInstance().getTeamContacts(LoginActivity.this, userID, token, shopiD, null);//获取团队列表
//                            LocNoticeController.getInstance().init(LoginActivity.this).requestLocTask();//获取区域信息
//                            UserVo userVo = UserFactory.getInstance().buildUserVo(sempLoginbean);
//                            UserDBUtil.getInstance().addUser(userVo);
//                            String avatarUrl = Constants.AVATAR_PRE_URL+userVo.getUserId()+".jpg";
//                            CacheUtil.getInstance().saveUserPhotoUrl(avatarUrl);
//                            Intent intent = new Intent(LoginActivity.this, MoreActivity.class);
//                            startActivity(intent);
//                            finish();
//                            overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
//                        } else {
//                            DialogUtil.getInstance().showToast(LoginActivity.this, "手机号还不是服务员 ");
//                        }
//                    }
//
//                    @Override
//                    public void beforeNetworkRequestStart() {
//
//                    }
//                });
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
        LogUtil.getInstance().info(LogLevel.INFO,"服务员开始登陆。。。");
        LoginController.getInstance().requestLogin(phone,true,new NetRequestListener() {
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                SempLoginBean sempLoginbean = new Gson().fromJson(result.rawResult, SempLoginBean.class);
                if (sempLoginbean.isSet()) {
                    //更新为最新的token和userid
                    CacheUtil.getInstance().setToken(sempLoginbean.getToken());
                    CacheUtil.getInstance().setUserId(sempLoginbean.getSalesid());
                    CacheUtil.getInstance().setUserPhone(phone);
                    CacheUtil.getInstance().setUserName(sempLoginbean.getName());
                    CacheUtil.getInstance().setShopID(sempLoginbean.getShopid());
                    CacheUtil.getInstance().setShopFullName(sempLoginbean.getFullname());
                    CacheUtil.getInstance().setLoginIdentity(IdentityType.WAITER);

                    String userID = CacheUtil.getInstance().getUserId();
                    String token  = CacheUtil.getInstance().getToken();
                    String shopiD = CacheUtil.getInstance().getShopID();

                    DBOpenHelper.DB_NAME = sempLoginbean.getSalesid() + ".db";

                    LoginController.getInstance().getDeptList(userID, token, shopiD);//获取部门列表
                    TeamContactsController.getInstance().getTeamContacts(LoginActivity.this, userID, token, shopiD, null);//获取团队列表
                    LocNoticeController.getInstance().init(LoginActivity.this).requestLocTask();//获取区域信息

                    UserVo userVo = UserFactory.getInstance().buildUserVo(sempLoginbean);
                    UserDBUtil.getInstance().addUser(userVo);
                    String avatarUrl = Constants.AVATAR_PRE_URL+userVo.getUserId()+".jpg";
                    CacheUtil.getInstance().saveUserPhotoUrl(avatarUrl);
                    Intent intent = new Intent(LoginActivity.this, MoreActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
                    LogUtil.getInstance().info(LogLevel.INFO, "服务员成功登陆。。。");
                } else {
                    DialogUtil.getInstance().showToast(LoginActivity.this, "手机号还不是服务员 ");
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
    }
}
