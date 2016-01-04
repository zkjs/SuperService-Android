package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import com.google.gson.Gson;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.set.TeamContactsController;
import com.zkjinshi.superservice.bean.AdminLoginBean;
import com.zkjinshi.superservice.bean.SempLoginBean;
import com.zkjinshi.superservice.factory.UserFactory;
import com.zkjinshi.superservice.manager.ZoneManager;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.DBOpenHelper;
import com.zkjinshi.superservice.sqlite.UserDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.MD5Util;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.UserVo;


/**
 * 应用启动页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SplashActivity extends Activity{

    public static final String TAG = SplashActivity.class.getSimpleName();

    private static final int SPLASH_DELAY_MILLIS = 3000;

    private static final int GO_LOGIN = 1000;
    private static final int GO_HOME = 1001;
    private static final int GO_GUIDE = 1002;

    private void initView(){

    }

    private void initData(){
        //判断当前网络状态
        if(NetWorkUtil.isNetworkConnected(this)){
            // 判断用户是否登录，如果登录则进入主页面
            if (CacheUtil.getInstance().isLogin()) {
                //启动应用静默处理数据
                silentProcessData();
                // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
                handler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
            } else  if (!CacheUtil.getInstance().isGuide()){
                handler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
            }else{
                handler.sendEmptyMessageDelayed(GO_LOGIN, SPLASH_DELAY_MILLIS);
            }
        }else {
            showNetDialog();
        }

    }

    private void initListeners(){

    }

    private void silentProcessData(){
        LoginController.getInstance().init(this);
        if(IdentityType.BUSINESS ==  CacheUtil.getInstance().getLoginIdentity()){
            LoginController.getInstance().requestAdminLogin(
                CacheUtil.getInstance().getUserPhone(),
                MD5Util.MD5(CacheUtil.getInstance().getPassword()),
                false,
                new ExtNetRequestListener(SplashActivity.this) {
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
                    super.onNetworkResponseSucceed(result);

                    if(null != result && !TextUtils.isEmpty(result.rawResult)){
                        Log.i(TAG, "result.rawResult:" + result.rawResult);
                        AdminLoginBean adminLoginBean = new Gson().fromJson(result.rawResult, AdminLoginBean.class);
                        if (adminLoginBean.isSet()) {
                            CacheUtil.getInstance().setToken(adminLoginBean.getToken());
                            CacheUtil.getInstance().setUserId(adminLoginBean.getUserid());
                            CacheUtil.getInstance().setUserPhone(CacheUtil.getInstance().getUserPhone());
                            CacheUtil.getInstance().setUserName(adminLoginBean.getName());
                            CacheUtil.getInstance().setShopID(adminLoginBean.getShopid());
                            CacheUtil.getInstance().setShopFullName(adminLoginBean.getFullname());
                            CacheUtil.getInstance().setLoginIdentity(IdentityType.BUSINESS);
                            CacheUtil.getInstance().setPassword(CacheUtil.getInstance().getPassword());
                            CacheUtil.getInstance().setLogin(true);
                            CacheUtil.getInstance().setAreaInfo(adminLoginBean.getLocid());
                            LoginController.getInstance().loginHxUser();
                            String userID = CacheUtil.getInstance().getUserId();
                            String token = CacheUtil.getInstance().getToken();
                            String shopiD = CacheUtil.getInstance().getShopID();
                            DBOpenHelper.DB_NAME = adminLoginBean.getUserid() + ".db";
                            LoginController.getInstance().getDeptList(userID, token, shopiD);//获取部门列表
                            TeamContactsController.getInstance().getTeamContacts(SplashActivity.this, userID, token, shopiD, null);//获取团队列表
                            ZoneManager.getInstance().requestMyZoneTask();//获取订阅区域
                            UserVo userVo = UserFactory.getInstance().buildUserVo(adminLoginBean);
                            UserDBUtil.getInstance().addUser(userVo);
                            String avatarUrl = ProtocolUtil.getShopLogoUrl(adminLoginBean.getShopid());
                            CacheUtil.getInstance().saveUserPhotoUrl(avatarUrl);
                        }
                    }
                }

                @Override
                public void beforeNetworkRequestStart() {

                }
            });
        }else{
            LoginController.getInstance().requestLogin(
                CacheUtil.getInstance().getUserPhone(),
                false,
                new ExtNetRequestListener(SplashActivity.this) {
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
                    super.onNetworkResponseSucceed(result);

                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    SempLoginBean sempLoginbean = new Gson().fromJson(result.rawResult, SempLoginBean.class);
                    if (sempLoginbean.isSet()) {
                        //更新为最新的token和userid
                        CacheUtil.getInstance().setToken(sempLoginbean.getToken());
                        CacheUtil.getInstance().setUserId(sempLoginbean.getSalesid());
                        CacheUtil.getInstance().setUserPhone(CacheUtil.getInstance().getUserPhone());
                        CacheUtil.getInstance().setUserName(sempLoginbean.getName());
                        CacheUtil.getInstance().setShopID(sempLoginbean.getShopid());
                        CacheUtil.getInstance().setShopFullName(sempLoginbean.getFullname());
                        CacheUtil.getInstance().setLoginIdentity(IdentityType.WAITER);
                        LoginController.getInstance().loginHxUser();
                        String userID = CacheUtil.getInstance().getUserId();
                        String token  = CacheUtil.getInstance().getToken();
                        String shopiD = CacheUtil.getInstance().getShopID();
                        DBOpenHelper.DB_NAME = sempLoginbean.getSalesid() + ".db";
                        LoginController.getInstance().getDeptList(userID, token, shopiD);//获取部门列表
                        TeamContactsController.getInstance().getTeamContacts(SplashActivity.this, userID, token, shopiD, null);//获取团队列表
                        ZoneManager.getInstance().requestMyZoneTask();//获取订阅区域
                        UserVo userVo = UserFactory.getInstance().buildUserVo(sempLoginbean);
                        UserDBUtil.getInstance().addUser(userVo);
                        String avatarUrl = Constants.GET_USER_AVATAR+userVo.getUserId()+".jpg";
                        CacheUtil.getInstance().saveUserPhotoUrl(avatarUrl);
                    }
                }

                @Override
                public void beforeNetworkRequestStart() {

                }
            });
        }
    }

    private void goGuide() {
        Intent guideIntent = new Intent(SplashActivity.this, GuideActivity.class);
        SplashActivity.this.startActivity(guideIntent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

    private void goLogin() {
        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
        SplashActivity.this.startActivity(loginIntent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

    private void goHome() {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GO_LOGIN:
                    goLogin();
                    break;
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initData();
        initListeners();
    }

    private void showNetDialog(){

        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setTitle("提示");
        customBuilder.setMessage("网络状态不好，稍后再试?");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        customBuilder.create().show();

    }
}
