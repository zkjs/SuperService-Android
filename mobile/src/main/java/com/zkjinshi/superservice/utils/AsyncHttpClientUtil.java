package com.zkjinshi.superservice.utils;

import android.content.Context;
import android.content.Intent;

import com.zkjinshi.superservice.activity.common.LoginActivity;
import com.zkjinshi.superservice.base.BaseApplication;


/**
 * Created by dujiande on 2016/3/21.
 */
public class AsyncHttpClientUtil {

    public static void onFailure(Context context, int statusCode){
        if(statusCode == 401){
            BaseApplication.getInst().clear();
            CacheUtil.getInstance().setLogin(false);
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }
}
