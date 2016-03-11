package com.zkjinshi.superservice.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.activity.common.LoginActivity;
import com.zkjinshi.superservice.bean.BaseBean;
import com.zkjinshi.superservice.utils.CacheUtil;

/**
 * 增加token失效跳转回登录页面的网络请求监听器
 * 开发者：JimmyZhang
 * 日期：2015/10/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public abstract class ExtNetRequestListener implements NetRequestListener{

    public static final String MTAG = ExtNetRequestListener.class.getSimpleName();

    private Context context;

    public ExtNetRequestListener(Context context){
        this.context = context;
    }

    @Override
    public void onNetworkRequestError(int errorCode, String errorMessage) {
        Log.i(MTAG,"errorCode:"+errorCode);
        Log.i(MTAG,"errorMessage:"+errorMessage);
    }

    @Override
    public void onNetworkRequestCancelled() {
        Log.i(MTAG,"onNetworkRequestCancelled");
    }

    @Override
    public void onNetworkResponseSucceed(NetResponse result) {
        try {

        }catch (Exception e){

        }
    }

    @Override
    public void beforeNetworkRequestStart() {
        Log.i(MTAG,"beforeNetworkRequestStart");
    }
}
