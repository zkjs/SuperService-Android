package com.zkjinshi.superservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zkjinshi.superservice.bean.LocPushBean;
import com.zkjinshi.superservice.notification.NotificationHelper;

import io.yunba.android.manager.YunBaManager;

/**
 * 开发者：JimmyZhang
 * 日期：2015/12/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class YunBaMessageReceiver extends BroadcastReceiver {

    public static final String TAG = YunBaMessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {

        //云巴推送处理
        if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {

            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String msg   = intent.getStringExtra(YunBaManager.MQTT_MSG);
            Log.i(TAG, "YunBaMessageReceiver-msg:"+msg);
            Log.i(TAG, "YunBaMessageReceiver-topic:"+topic);
            LocPushBean locPushBean = null;

            try {
                locPushBean = new Gson().fromJson(msg, LocPushBean.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }

            if(null != locPushBean){
                //弹出当前位置到达提示
                NotificationHelper.getInstance().showNotification(context, locPushBean);
            }

        } else if(YunBaManager.PRESENCE_RECEIVED_ACTION.equals(intent.getAction())) {
            String topic   = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String payload = intent.getStringExtra(YunBaManager.MQTT_MSG);
            StringBuilder showMsg = new StringBuilder();
            showMsg.append("Received message presence: ").append(YunBaManager.MQTT_TOPIC)
                    .append(" = ").append(topic).append(" ")
                    .append(YunBaManager.MQTT_MSG).append(" = ").append(payload);
            Log.d(TAG, showMsg.toString());
        }
    }
}
