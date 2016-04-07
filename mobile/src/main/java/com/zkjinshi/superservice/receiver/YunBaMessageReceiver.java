package com.zkjinshi.superservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.emchat.EMConversationHelper;
import com.zkjinshi.superservice.ext.vo.AmountStatusVo;
import com.zkjinshi.superservice.manager.SSOManager;
import com.zkjinshi.superservice.manager.YunBaSubscribeManager;
import com.zkjinshi.superservice.notification.NotificationHelper;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.ActiveCodeNoticeVo;
import com.zkjinshi.superservice.vo.YunBaMsgVo;

import org.json.JSONException;
import org.json.JSONObject;

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

            try {
                String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
                String msg   = intent.getStringExtra(YunBaManager.MQTT_MSG);
                Log.i(TAG, "YunBaMessageReceiver-msg:"+msg);
                Log.i(TAG, "YunBaMessageReceiver-topic:"+topic);
                JSONObject jsonObject = new JSONObject(msg);
                String type = jsonObject.getString("type");
                String data = jsonObject.getString("data");
                //PAYMENT_RESULT | PAYMENT_CONFIRM | ARRIVING
                if("PAYMENT_RESULT".equals(type)){//刷脸支付
                    AmountStatusVo amountStatusVo = new Gson().fromJson(data,AmountStatusVo.class);
                    if(null != amountStatusVo){
                        NotificationHelper.getInstance().showNotification(context, amountStatusVo);
                    }
                }else if("ARRIVING".equals(type)){//到店通知（GPS/蓝牙）
                    YunBaMsgVo yunBaMsgVo = new Gson().fromJson(data, YunBaMsgVo.class);
                    String arriveType = yunBaMsgVo.getType();
                    if("GPS".equals(arriveType)){
                        LogUtil.getInstance().info(LogLevel.ERROR,"收到GPS分级推送");
                        LogUtil.getInstance().info(LogLevel.ERROR,"推送内容:"+data);
                    }
                    //是否注册接受到店通知
                    if(null != yunBaMsgVo){
                        //弹出当前位置到达提示
                        NotificationHelper.getInstance().showNotification(context, yunBaMsgVo);
                    }


                    //如果是商家中心，则执行发送消息给客人
                    if(SSOManager.getInstance().isShopCenter()){//1、检测用户身份，是否为消息中心
                        if(null != yunBaMsgVo){//2、发送欢迎信息给客人
                            String userId = yunBaMsgVo.getUserId();
                            String userName = yunBaMsgVo.getUserName();
                            EMConversationHelper.getInstance().sendWelcomeMessage(userId,userName);
                        }
                    }
                }else if("ACTIVATESALECODE".equals(type)){//激活邀请码
                    ActiveCodeNoticeVo activeCodeNoticeVo = new Gson().fromJson(data,ActiveCodeNoticeVo.class);
                    if(null != activeCodeNoticeVo){
                        Intent receiver = new Intent();
                        receiver.setAction("com.zkjinshi.invite_code");
                        context.sendBroadcast(receiver);
                        NotificationHelper.getInstance().showNotification(context,activeCodeNoticeVo);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
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
