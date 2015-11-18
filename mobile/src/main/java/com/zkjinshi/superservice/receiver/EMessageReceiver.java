package com.zkjinshi.superservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zkjinshi.superservice.activity.set.ClientBindController;
import com.zkjinshi.superservice.bean.ClientBaseBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.notification.NotificationHelper;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null ){
            String action = intent.getAction();
            if(!TextUtils.isEmpty(action) && action.equals("com.zkjinshi.svip.ACTION_INVITE")){

                String userID   = intent.getStringExtra("userId");
                String userName = intent.getStringExtra("userName");
                String mobileNo = intent.getStringExtra("mobileNo");
                long   datetime = intent.getLongExtra("date", 0);

                ClientBaseBean clientBase = new ClientBaseBean();
                clientBase.setUserid(userID);
                clientBase.setUsername(userName);
                clientBase.setPhone(mobileNo);

                NotificationHelper.getInstance().showNotification(context, clientBase, datetime);

                ClientBindController.getInstance().bindClient(
                        clientBase,
                        new ExtNetRequestListener() {
                            @Override
                            public void onNetworkRequestError(int errorCode, String errorMessage) {
                                super.onNetworkRequestError(errorCode, errorMessage);
                            }

                            @Override
                            public void onNetworkRequestCancelled() {
                                super.onNetworkRequestCancelled();
                            }

                            @Override
                            public void onNetworkResponseSucceed(NetResponse result) {
                                super.onNetworkResponseSucceed(result);
                            }

                            @Override
                            public void beforeNetworkRequestStart() {
                                super.beforeNetworkRequestStart();
                            }
                        });
            }
        }

    }

}
