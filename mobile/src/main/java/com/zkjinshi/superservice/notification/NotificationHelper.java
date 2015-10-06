package com.zkjinshi.superservice.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.base.util.VibratorHelper;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.ChatActivity;
import com.zkjinshi.superservice.activity.common.MainActivity;
import com.zkjinshi.superservice.activity.common.SplashActivity;
import com.zkjinshi.superservice.entity.MsgPushLocAd;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.MediaPlayerUtil;
import com.zkjinshi.superservice.vo.MessageVo;
import com.zkjinshi.superservice.vo.MimeType;

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
            // 2.设置点击跳转事件
            Intent intent = new Intent(context, SplashActivity.class);
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
     * 通知栏通知用户到店
     *
     * @param context
     * @param msgPushLocAd
     */
    public void showNotification(Context context, MsgPushLocAd msgPushLocAd) {

        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle("" + msgPushLocAd.getAndtitle());
        notificationBuilder.setContentText("" + msgPushLocAd.getAndalert());
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
        notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }

    private NotificationCompat.WearableExtender extendWear(
            Context context,NotificationCompat.Builder builder,MessageVo messageVo) {
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
        String shopId = messageVo.getShopId();
        String sessionId = messageVo.getSessionId();
        // 1、增加语音快捷回复
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setAction("waiter.intent.action.RELAY");
        intent.putExtra("session_id", sessionId);
        intent.putExtra("shop_id",shopId);
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
