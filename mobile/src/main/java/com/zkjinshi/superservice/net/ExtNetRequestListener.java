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
public abstract class ExtNetRequestListener  implements NetRequestListener{

    public static final String TAG = ExtNetRequestListener.class.getSimpleName();

    private Context context;

    public ExtNetRequestListener(Context context){
        this.context = context;
    }

    public ExtNetRequestListener() {

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

        try {
            BaseBean baseBean = new Gson().fromJson(result.rawResult, BaseBean.class);
            if (baseBean != null && !baseBean.isSet() && baseBean.getErr().equals("400")) {
                if (context instanceof Activity) {
                    DialogUtil.getInstance().showToast(context, "Token失效，请重新登录!");
                    CacheUtil.getInstance().setLogin(false);
                    Intent intent = new Intent(context, LoginActivity.class);
                    ((Activity) context).finish();
                    context.startActivity(intent);
                }
            }
        }catch (Exception e){
           // Log.e(TAG,e.getMessage());
        }
    }

    @Override
    public void beforeNetworkRequestStart() {
        Log.i(TAG,"beforeNetworkRequestStart");
    }
}
