package com.zkjinshi.superservice.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.text.TextUtils;

import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.base.util.VibratorHelper;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.group.ChatGroupActivity;
import com.zkjinshi.superservice.activity.chat.single.ChatActivity;
import com.zkjinshi.superservice.activity.common.LoginActivity;
import com.zkjinshi.superservice.activity.common.MainActivity;
import com.zkjinshi.superservice.activity.common.SplashActivity;
import com.zkjinshi.superservice.activity.set.ClientActivity;
import com.zkjinshi.superservice.bean.ClientBaseBean;
import com.zkjinshi.superservice.bean.LocPushBean;
import com.zkjinshi.superservice.emchat.EMConversationHelper;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.MediaPlayerUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.TxtExtType;

import java.text.SimpleDateFormat;
import java.util.Date;

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
     * @param context
     * @param event
     */
    public void showNotification(Context context, EMNotifierEvent event) {
        int nofifyFlag = 0;
        switch (event.getEvent()) {
            case EventNewMessage:
                EMMessage message = (EMMessage) event.getData();
                if(null != message){
                    String username = message.getFrom();
                    String titleName = null;
                    if(!username.equals(CacheUtil.getInstance().getUserId())){
                        EMMessage.Type msgType = message.getType();
                        if (ActivityManagerHelper.isRunningBackground(context)) {
                            //是否发送绑定客户消息
                            boolean bindClient = false;
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
                            if (message.getChatType() == EMMessage.ChatType.GroupChat ||
                                message.getChatType() == EMMessage.ChatType.ChatRoom) {

                                EMConversationHelper.getInstance().requestGroupListTask();
                                String groupId = message.getTo();
                                EMGroup group = EMGroupManager.getInstance().getGroup(groupId);
                                if (group != null){
                                    titleName = group.getGroupName();
                                }
                            } else {
                                try {
                                    bindClient = message.getBooleanAttribute("bindClient");
                                    String fromName = message.getStringAttribute("fromName");
                                    String toName   = message.getStringAttribute("toName");
                                    if(!TextUtils.isEmpty(fromName) && !fromName.equals(
                                        CacheUtil.getInstance().getUserName())){
                                        titleName = fromName;
                                    }else{
                                        if(!TextUtils.isEmpty(toName)){
                                            titleName = toName;
                                        }
                                    }
                                } catch (EaseMobException e) {
                                    e.printStackTrace();
                                }
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

                            //TODO：用户绑定消息进入我的联系人
                            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                            // 2.设置点击跳转事件
                            Intent intent = null;
                            if(bindClient){
                                intent = new Intent(context, ClientActivity.class);
                            } else {
                                CacheUtil.getInstance().setCurrentItem(1);
                                intent = new Intent(context, MainActivity.class);
                            }
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                            notificationBuilder.setContentIntent(pendingIntent);
                            // 3.设置通知栏其他属性
                            notificationBuilder.setAutoCancel(true);
                            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                            //4、设置手表特有属性
                            notificationBuilder.extend(extendWear(context, notificationBuilder,message));
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
     * @param locPushBean
     */
    public void showNotification(Context context, LocPushBean locPushBean) {
        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        String contactName = locPushBean.getUsername();
        int sex = locPushBean.getSex();
        String locDesc = locPushBean.getLocdesc();
        notificationBuilder.setContentTitle("" + context.getString(R.string.app_name));
        String welcomeMsg = "";
        if(sex == 0){
            welcomeMsg = contactName+"女士到达"+locDesc;
        }else {
            welcomeMsg = contactName+"先生到达"+locDesc;
        }
        notificationBuilder.setContentText(welcomeMsg);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        String contactId = locPushBean.getUserid();
        String imageUrl =  ProtocolUtil.getAvatarUrl(contactId);
        ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(context,36), DisplayUtil.dip2px(context,36));
        Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageUrl,imageSize);
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
     * @param clientBase
     * @param dateTime
     */
    public void showNotification(Context context, ClientBaseBean clientBase, long dateTime) {

        Intent receiver = new Intent();
        receiver.setAction("com.zkjinshi.invite_code");
        context.sendBroadcast(receiver);

        String time = TimeUtil.getChatTime(dateTime);
        String userName = clientBase.getUsername();
        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        String content = context.getString(R.string.user) + " " + userName + " 于 "+ time +
                         context.getString(R.string.use_your_invite_code);
        notificationBuilder.setContentTitle("邀请码通知");
        notificationBuilder.setContentText(content);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        String fromID    = clientBase.getUserid();
        String avatarUrl = ProtocolUtil.getAvatarUrl(fromID);
        Bitmap bitmap    = ImageLoader.getInstance().loadImageSync(avatarUrl);
        notificationBuilder.setLargeIcon(bitmap);

        // 2.设置点击跳转事件
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);

        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                                                                               context);
        notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }

    /**
     * 通知提示:客户退出当前账户
     * @param context
     */
    public void showExitAccountNotification(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        String content = "您的账号于" + sdf.format(new Date()) + "在另一台设备登录";
        notificationBuilder.setContentTitle("下线通知");
        notificationBuilder.setContentText(content);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);

        String userID    = CacheUtil.getInstance().getUserId();
        String avatarUrl = ProtocolUtil.getAvatarUrl(userID);
        Bitmap bitmap    = ImageLoader.getInstance().loadImageSync(avatarUrl);
        notificationBuilder.setLargeIcon(bitmap);

        // 2.设置点击跳转事件
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
            Context context,NotificationCompat.Builder builder,EMMessage message) {
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
        // 1、增加语音快捷回复
        Intent intent = new Intent();
        if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
            String groupId = message.getTo();
            intent.setClass(context, ChatGroupActivity.class);
            intent.putExtra("groupId",groupId);
        }else {
            String username = message.getUserName();
            intent.setClass(context, ChatActivity.class);
            intent.putExtra(Constants.EXTRA_USER_ID, username);
            try {
                intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                String shopId = CacheUtil.getInstance().getShopID();
                String shopName = CacheUtil.getInstance().getShopFullName();
                String fromName = message.getStringAttribute("fromName");
                String toName = message.getStringAttribute("toName");
                if (!TextUtils.isEmpty(shopId)) {
                    intent.putExtra(Constants.EXTRA_SHOP_ID, shopId);
                }
                if (!TextUtils.isEmpty(shopName)) {
                    intent.putExtra(Constants.EXTRA_SHOP_NAME, shopName);
                }
                if (!toName.equals(CacheUtil.getInstance().getUserName())) {
                    intent.putExtra(Constants.EXTRA_TO_NAME, toName);
                } else {
                    intent.putExtra(Constants.EXTRA_TO_NAME, fromName);
                }
            } catch (EaseMobException e) {
                e.printStackTrace();
            }
        }
        intent.setAction(Constants.ACTION_VOICE_RELAY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    /**
     * 展示销售已绑定提示
     * @param context
     */
    public void showSalerBindedMessage(Context context, String userID, String userName) {

        NotificationCompat.Builder notificationBuilder = null;
        // 1.设置显示信息
        notificationBuilder = new NotificationCompat.Builder(context);
        String content = "客户" + userName + "已经添加您为联系人";
        notificationBuilder.setContentTitle(userName);
        notificationBuilder.setContentText(content);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        String avatarUrl = ProtocolUtil.getAvatarUrl(userID);
        Bitmap bitmap    = ImageLoader.getInstance().loadImageSync(avatarUrl);
        notificationBuilder.setLargeIcon(bitmap);

        // 2.设置点击跳转事件
        Intent intent = new Intent(context, ClientActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);

        // 3.设置通知栏其他属性
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
    }
}
