package com.zkjinshi.superservice.pad.activity.common;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;

import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.pad.utils.CacheUtil;
import com.zkjinshi.superservice.pad.R;

import com.zkjinshi.superservice.pad.base.BaseActivity;
import com.zkjinshi.superservice.pad.manager.SSOManager;
import com.zkjinshi.superservice.pad.manager.YunBaSubscribeManager;

import org.json.JSONObject;


/**
 * 应用启动页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SplashActivity extends BaseActivity{

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
                // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
                handler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
            } else  if (!CacheUtil.getInstance().isGuide()){
                //取消云巴频道订阅
                YunBaSubscribeManager.getInstance().unSubscribe(this,null);
                //取消云巴别名订阅
                YunBaSubscribeManager.getInstance().cancelAlias(this);
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

        //增加sso静默登录
        SSOManager.getInstance().requestRefreshToken(this, new SSOManager.SSOCallBack() {
            @Override
            public void onNetworkResponseSucceed() {
                getUserInfo();
            }
        });

    }

    private void getUserInfo() {
        LoginController.getInstance().getUserInfo(this, CacheUtil.getInstance().getUserId(), new LoginController.CallBackListener() {
            @Override
            public void successCallback(JSONObject response) {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
                overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });
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
        //启动应用静默处理数据
        silentProcessData();

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
