package com.zkjinshi.superservice.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.activity.common.LoginActivity;
import com.zkjinshi.superservice.base.BaseApplication;
import com.zkjinshi.superservice.bean.BaseBean;
import com.zkjinshi.superservice.emchat.EasemobIMHelper;
import com.zkjinshi.superservice.manager.YunBaSubscribeManager;
import com.zkjinshi.superservice.utils.CacheUtil;

/**
 * 增加token失效跳转回登录页面的网络请求监听器
 * 开发者：JimmyZhang
 * 日期：2015/10/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public abstract class ExtNetRequestListener implements NetRequestListener{

    public static final String TAG = ExtNetRequestListener.class.getSimpleName();

    private Context context;

    public ExtNetRequestListener(Context context){
        this.context = context;
    }

    @Override
    public void onNetworkRequestError(int errorCode, String errorMessage) {
        Log.i(TAG,"errorCode:"+errorCode);
        Log.i(TAG,"errorMessage:"+errorMessage);
    }

    @Override
    public void onNetworkRequestCancelled() {
        Log.i(TAG,"onNetworkRequestCancelled");
    }

    @Override
    public void onNetworkResponseSucceed(NetResponse result) {

    }

    @Override
    public void beforeNetworkRequestStart() {
        Log.i(TAG,"beforeNetworkRequestStart");
    }

    @Override
    public void onCookieExpired() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //移除云巴订阅推送
                YunBaSubscribeManager.getInstance().unSubscribe(context,null);
                //取消订阅别名
                YunBaSubscribeManager.getInstance().cancelAlias(context);
                //环信接口退出
                EasemobIMHelper.getInstance().logout();
                CacheUtil.getInstance().setLogin(false);
                BaseApplication.getInst().clear();
                Intent intent = new Intent(context,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ((Activity)context).startActivity(intent);
            }
        });
    }
}
