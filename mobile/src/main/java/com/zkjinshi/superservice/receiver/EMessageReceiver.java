package com.zkjinshi.superservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(!TextUtils.isEmpty(action) && action.equals("com.zkjinshi.svip.ACTION_INVITE")){

        }
    }

}
