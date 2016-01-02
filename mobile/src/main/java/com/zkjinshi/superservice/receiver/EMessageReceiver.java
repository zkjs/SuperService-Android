package com.zkjinshi.superservice.receiver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;

import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.activity.common.LoginActivity;
import com.zkjinshi.superservice.bean.ClientBaseBean;
import com.zkjinshi.superservice.notification.NotificationHelper;
import com.zkjinshi.superservice.utils.CacheUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

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
            if(!TextUtils.isEmpty(action)){
                if( action.equals("com.zkjinshi.superservice.ACTION_INVITE")){

                    String userID   = intent.getStringExtra("userId");
                    String userName = intent.getStringExtra("userName");
                    String mobileNo = intent.getStringExtra("mobileNo");
                    long   datetime = intent.getLongExtra("date", 0);
                    ClientBaseBean clientBase = new ClientBaseBean();
                    clientBase.setUserid(userID);
                    clientBase.setUsername(userName);
                    clientBase.setPhone(mobileNo);
                    //提示用户邀请码被绑定
                    NotificationHelper.getInstance().showNotification(context, clientBase, datetime);
                }else if(action.equals("com.zkjinshi.superservice.CONNECTION_CONFLICT")){
                    showOfflineDialog(context);
                    //提示用户邀请码被绑定
                    NotificationHelper.getInstance().showExitAccountNotification(context);
                }
            }
        }
    }

    /**
     * 下线通知
     * @param context
     */
    private synchronized void showOfflineDialog(final Context context) {
        Dialog dialog = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("下线通知");
        customBuilder.setMessage("您的账号于" + sdf.format(new Date()) + "在另一台设备登录");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                WebSocketManager.getInstance().logoutIM(context);
                CacheUtil.getInstance().setLogin(false);
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        dialog = customBuilder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }


}
