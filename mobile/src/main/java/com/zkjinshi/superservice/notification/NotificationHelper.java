package com.zkjinshi.superservice.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.view.Gravity;

import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.VibratorHelper;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.ChatActivity;
import com.zkjinshi.superservice.activity.common.InviteCodesActivity;
import com.zkjinshi.superservice.activity.common.MainActivity;
import com.zkjinshi.superservice.activity.common.SplashActivity;
import com.zkjinshi.superservice.activity.set.ClientBindActivity;
import com.zkjinshi.superservice.activity.set.ClientSelectActivity;
import com.zkjinshi.superservice.bean.ClientBaseBean;
import com.zkjinshi.superservice.bean.InviteCode;
import com.zkjinshi.superservice.entity.InviteCodeEntity;
import com.zkjinshi.superservice.entity.MsgPushTriggerLocNotificationM2S;
import com.zkjinshi.superservice.entity.MsgUserDefine;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.MediaPlayerUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.MessageVo;
import com.zkjinshi.superservice.vo.MimeType;
import com.zkjinshi.superservice.vo.TxtExtType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 消息通知帮助类
 * 开发者：JimmyZhang
 * 日期：2015/8/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NotificationHelper {

    public static int NOTIFY_ID = 1;

    private NotificationHelper() {
    }

    private static NotificationHelper instance;

    public synchronized static NotificationHelper getInstance() {
        if (null == instance) {
            instance = new NotificationHelper();
        }
        return instance;
    }

    /**
     * 后台通知栏通知用户收到消息
     *
     * @param context
     * @param messageVo
     */
    public void showNotification(Context context, MessageVo messageVo) {
        if (ActivityManagerHelper.isRunningBackground(context)) {
            int nofifyFlag = 0;
            NotificationCompat.Builder notificationBuilder = null;
            // 1.设置显示信息
            notificationBuilder = new NotificationCompat.Builder(context);
            notificationBuilder.setContentTitle("" + messageVo.getContactName());
            MimeType msgType = messageVo.getMimeType();
            if (msgType == MimeType.TEXT) {
                notificationBuilder.setContentText("" + messageVo.getContent());
            } else if (msgType == MimeType.IMAGE) {
                notificationBuilder.setContentText("[图片]");
            } else {
                notificationBuilder.setContentText("[语音]");
            }
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            String contactId = messageVo.getContactId();
            String imageUrl = Constants.GET_USER_AVATAR + contactId + ".jpg";
            Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageUrl);
            notificationBuilder.setLargeIcon(bitmap);
            // 2.设置点击跳转事件
            Intent intent = new Intent(context, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, 0);
            notificationBuilder.setContentIntent(pendingIntent);
            // 3.设置通知栏其他属性
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
            //4、设置手表特有属性
            notificationBuilder.extend(extendWear(context, notificationBuilder,messageVo));
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            notificationManager.notify(nofifyFlag, notificationBuilder.build());
        } else {
            MediaPlayerUtil.playNotifyVoice(context);
            VibratorHelper.vibratorShark(context);
        }
    }

    /**
     * 后台通知栏通知用户收到消息
     * @param context
     * @param event
     */
    public void showNotification(Context context, EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
                EMMessage message = (EMMessage) event.getData();
                if(null != message){
                    String username = message.getFrom();
                    String titleName = null;
                    if(!username.equals(CacheUtil.getInstance().getUserId())){
                        EMMessage.Type msgType = message.getType();
                        if (ActivityManagerHelper.isRunningBackground(context)) {
                            int nofifyFlag = 0;
                            NotificationCompat.Builder notificationBuilder = null;
                            // 1.设置显示信息
                            notificationBuilder = new NotificationCompat.Builder(context);
                            if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                                //TODO JIMMY 后续需要修改
                                titleName = message.getTo();
                            } else {
                                titleName = message.getFrom();
                            }
                            notificationBuilder.setContentTitle("" + titleName);
                            if (msgType == EMMessage.Type.TXT) {
                                try {
                                    int extType = message.getIntAttribute(Constants.MSG_TXT_EXT_TYPE);
                                    if(TxtExtType.DEFAULT.getVlaue() == extType){
                                        TextMessageBody txtBody = (TextMessageBody) message.getBody();
                                        String content = txtBody.getMessage();
                                        notificationBuilder.setContentText("" + content);
                                    }else{
                                        notificationBuilder.setContentText("[订单]");
                                    }
                                } catch (EaseMobException e) {
                                    e.printStackTrace();
                                }

                            } else if (msgType == EMMessage.Type.IMAGE) {
                                notificationBuilder.setContentText("[图片]");
                            } else if(msgType ==  EMMessage.Type.VOICE){
                                notificationBuilder.setContentText("[语音]");
                            }
                            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                            // 2.设置点击跳转事件
                            Intent intent = new Intent(context, MainActivity.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                                    intent, 0);
                            notificationBuilder.setContentIntent(pendingIntent);
                            // 3.设置通知栏其他属性
                            notificationBuilder.setAutoCancel(true);
                            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                            NotificationManagerCompat notificationManager =
                                    NotificationManagerCompat.from(context);
                            notificationManager.notify(nofifyFlag, notificationBuilder.build());
                        } else {
                            MediaPlayerUtil.playNotifyVoice(context);
                            VibratorHelper.vibratorShark(context);
                        }
                    }
                }
                break;
        }
    }

    /**
     * 接收到店通知
     *
     * @param context
     * @param msgPushTriggerLocNotificationM2S
     */
    public void showNotification(Context context, MsgPushTriggerLocNotificationM2S msgPushTriggerLocNotificationM2S) {
            NotificationCompat.Builder notificationBuilder = null;
            // 1.设置显示信息
            notificationBuilder = new NotificationCompat.Builder(context);
            String contactName = msgPushTriggerLocNotificationM2S.getUsername();
            notificationBuilder.setContentTitle("" + contactName);
            notificationBuilder.setContentText("欢迎" + contactName+"先生/女士光临酒店");
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            String contactId = msgPushTriggerLocNotificationM2S.getUserid();
            String imageUrl = Constants.GET_USER_AVATAR + contactId + ".jpg";
            Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageUrl);
            notificationBuilder.setLargeIcon(bitmap);
            // 2.设置点击跳转事件
            Intent intent = new Intent(context, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, 0);
            notificationBuilder.setContentIntent(pendingIntent);
            // 3.设置通知栏其他属性
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }

    /**
     * 通知提示:客户使用邀请码成功
     * @param context
     * @param msgUserDefine
     */
    public void showNotification(Context context, MsgUserDefine msgUserDefine) {

        String inviteCodeEntityJson = msgUserDefine.getContent();
        Gson gson = new Gson();
        InviteCodeEntity inviteCodeEntity = gson.fromJson(inviteCodeEntityJson,
                InviteCodeEntity.class);

        String userID   = inviteCodeEntity.getUserid();
        String phoneNum = inviteCodeEntity.getPhone_number();
        String userName = inviteCodeEntity.getUsername();

        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        String pushAlert = msgUserDefine.getPushalert();

        String content = context.getString(R.string.user) + userName + context.getString(
                R.string.use_your_invite_code_and_add_you_as_exclusive_server_success);

        notificationBuilder.setContentTitle("" + pushAlert);
        notificationBuilder.setContentText(content);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        String fromID    = msgUserDefine.getFromid();
        String avatarUrl = ProtocolUtil.getAvatarUrl(fromID);
        Bitmap bitmap    = ImageLoader.getInstance().loadImageSync(avatarUrl);
        notificationBuilder.setLargeIcon(bitmap);

        // 2.设置点击跳转事件
        Intent intent = new Intent(context, ClientSelectActivity.class);
        intent.putExtra("phone_number", phoneNum);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);

        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                                                                               context);
        notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }

    private NotificationCompat.WearableExtender extendWear(
            Context context,NotificationCompat.Builder builder,MessageVo messageVo) {
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
        String shopId = messageVo.getShopId();
        String sessionId = messageVo.getSessionId();
        // 1、增加语音快捷回复
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setAction(Constants.ACTION_VOICE_RELAY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("session_id", sessionId);
        intent.putExtra("shop_id", shopId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        String replyLabel = context.getResources().getString(R.string.reply_label);
        String[] replyChoices = context.getResources().getStringArray(R.array.reply_choices);
        RemoteInput remoteInput = new RemoteInput.Builder(Constants.EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .setChoices(replyChoices)
                .build();
        String voiceReply = context.getResources().getString(R.string.voice_reply);
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.mipmap.yuyin,
                voiceReply, pendingIntent)
                .addRemoteInput(remoteInput)
                .build();
        wearableExtender.addAction(replyAction);
        return wearableExtender;
    }

}
