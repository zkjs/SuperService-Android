package com.zkjinshi.superservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zkjinshi.base.util.Constants;
import com.zkjinshi.superservice.emchat.EasemobIMManager;
import com.zkjinshi.superservice.manager.YunBaSubscribeManager;
import com.zkjinshi.superservice.utils.CacheUtil;

/**
 * 开机启动
 * 开发者：JimmyZhang
 * 日期：2016/3/15
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent data) {
        String action = data.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i(Constants.ZKJINSHI_BASE_TAG, "开机自动启动SuperService.....");
            if(CacheUtil.getInstance().isLogin()){
                //登陆环信
                Log.i(Constants.ZKJINSHI_BASE_TAG, "开机自动登陆环信.....");
                EasemobIMManager.getInstance().loginHxUser();
                //订阅云巴别名
                Log.i(Constants.ZKJINSHI_BASE_TAG, "开机自动订阅云巴别名.....");
                YunBaSubscribeManager.getInstance().setAlias(context);
                //根据角色订阅频道
                Log.i(Constants.ZKJINSHI_BASE_TAG, "开机自动根据角色订阅云巴频道.....");
                YunBaSubscribeManager.getInstance().subscribe();
                //启动激活服务
               /* Log.i(Constants.ZKJINSHI_BASE_TAG, "开机自动启动激活服务.....");
                Intent intent = new Intent(context, ActiveService.class);
                intent.setAction("com.zkjinshi.superservice.ACTION_ACTIVE");
                context.startService(intent);*/
            }
        }
    }
}
