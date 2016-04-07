package com.zkjinshi.superservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;

/**
 * 维持应用激活状态服务
 * 开发者：JimmyZhang
 * 日期：2016/4/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ActiveService extends Service{

    public static String TAG = ActiveService.class.getSimpleName();
    public static boolean killMyself = false;
    public Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"ActiveService.onCreate()");
        LogUtil.getInstance().info(LogLevel.WARN,"ActiveService.onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"ActiveService.onStartCommand()");
        LogUtil.getInstance().info(LogLevel.WARN,"ActiveService.onStartCommand()");
        this.intent = intent;
        if(intent == null){
            Log.d(TAG,"intent == null");
        }
        if(killMyself){
            return START_NOT_STICKY;
        }else{
            return  START_REDELIVER_INTENT;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"ActiveService.onDestroy()");
        LogUtil.getInstance().info(LogLevel.WARN,"ActiveService.onDestroy()");
        if(killMyself){

        }else if(intent != null){
            getApplicationContext().startService(intent);
        }
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
