package com.zkjinshi.superservice;

import android.app.Application;
import android.content.Context;

/**
 * 超级服务入口
 * 开发者：JimmyZhang
 * 日期：2015/9/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceApplication extends Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this.getApplicationContext();

    }

    public static Context getContext(){
        return mContext;
    }
}
