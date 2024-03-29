package com.zkjinshi.superservice.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.text.TextUtils;
import android.view.View;

import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
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
import com.zkjinshi.superservice.ext.activity.facepay.AmountDetailActivity;
import com.zkjinshi.superservice.ext.vo.AmountStatusVo;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.MediaPlayerUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ActiveCodeNoticeVo;
import com.zkjinshi.superservice.vo.ItemTagVo;
import com.zkjinshi.superservice.vo.TxtExtType;
import com.zkjinshi.superservice.vo.YunBaMsgVo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ytx.org.apache.http.impl.auth.SPNegoScheme;

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
            case EventOfflineMessage:
            case EventNewMessage:
                shopMessageNotification(context, event, nofifyFlag);
            break;
        }
    }

    /**
     * 展示消息通知
     */
    private void shopMessageNotification(Context context,  EMNotifierEvent event, int nofifyFlag) {
        EMMessage message = (EMMessage) event.getData();
        if(null != message){
            String username = message.getFrom();
            String titleName = null;
            if(!username.equals(CacheUtil.getInstance().getUserId())){
                EMMessage.Type msgType = message.getType();
                //是否发送绑定客户消息
                boolean bindClient = false;
                NotificationCompat.Builder notificationBuilder = null;
                notificationBuilder = new NotificationCompat.Builder(context);
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

                            bindClient = message.getBooleanAttribute("bindClient");
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
                    //后台运行
                    if (ActivityManagerHelper.isRunningBackground(context)) {
                        CacheUtil.getInstance().setCurrentItem(1);
                        intent = new Intent(context, MainActivity.class);
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
        }
    }

    /**
     * 接收云巴到店通知
     * @param context
     * @param yunBaMsgVo
     */
    public synchronized void showNotification(final Context context, final YunBaMsgVo yunBaMsgVo){
        ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(context, 36),
                DisplayUtil.dip2px(context, 36));
        String imageSuffix = yunBaMsgVo.getUserImage();
        String imageUrl  = ProtocolUtil.getAvatarUrl(context,imageSuffix);
        NonViewAware aware = new NonViewAware(imageSize, ViewScaleType.CROP);
        ImageLoader.getInstance().displayImage(imageUrl,aware,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                String title = yunBaMsgVo.getAlert();
                if(!TextUtils.isEmpty(title)){
                    notificationBuilder.setContentTitle(title);
                }
                ArrayList<ItemTagVo> tagList =  yunBaMsgVo.getTags();
                StringBuffer contentSb = new StringBuffer();
                String contentStr = null;
                if(null != tagList && !tagList.isEmpty()){
                    String tagName = null;
                    for(ItemTagVo itemTagVo : tagList){
                        tagName = itemTagVo.getTagname();
                        if(!TextUtils.isEmpty(tagName)){
                            contentSb.append(tagName).append("、");
                        }
                    }
                    contentStr = contentSb.substring(0,contentSb.length()-1);
                    notificationBuilder.setContentText(contentStr);
                }
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
                String reason = null;
                switch (failReason.getType()) {
                    case IO_ERROR:// 文件I/O错误
                        reason = "文件I/O错误";
                        break;
                    case DECODING_ERROR:// 解码错误
                        reason = "解码错误";
                        break;
                    case NETWORK_DENIED:// 网络延迟
                        reason = "网络延迟";
                        break;
                    case OUT_OF_MEMORY:// 内存不足
                        reason = "内存不足";
                        break;
                    case UNKNOWN:// 原因不明
                        reason = "原因不明";
                        break;
                }
                LogUtil.getInstance().info(LogLevel.WARN,"收到到店通通知加载图片失败:"+reason);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                String content = yunBaMsgVo.getAlert();
                if(!TextUtils.isEmpty(content)){
                    notificationBuilder.setContentTitle(content);
                }
                ArrayList<ItemTagVo> tagList =  yunBaMsgVo.getTags();
                StringBuffer contentSb = new StringBuffer();
                String contentStr = null;
                if(null != tagList && !tagList.isEmpty()){
                    String tagName = null;
                    for(ItemTagVo itemTagVo : tagList){
                        tagName = itemTagVo.getTagname();
                        if(!TextUtils.isEmpty(tagName)){
                            contentSb.append(tagName).append("、");
                        }
                    }
                    contentStr = contentSb.substring(0,contentSb.length()-1);
                    notificationBuilder.setContentText(contentStr);
                }
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setLargeIcon(loadedImage);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(loadedImage));
                }
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
                LogUtil.getInstance().info(LogLevel.WARN,"收到到店通通知加载图片成功");
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                String content = yunBaMsgVo.getAlert();
                if(!TextUtils.isEmpty(content)){
                    notificationBuilder.setContentTitle(content);
                }
                ArrayList<ItemTagVo> tagList =  yunBaMsgVo.getTags();
                StringBuffer contentSb = new StringBuffer();
                String contentStr = null;
                if(null != tagList && !tagList.isEmpty()){
                    String tagName = null;
                    for(ItemTagVo itemTagVo : tagList){
                        tagName = itemTagVo.getTagname();
                        if(!TextUtils.isEmpty(tagName)){
                            contentSb.append(tagName).append("、");
                        }
                    }
                    contentStr = contentSb.substring(0,contentSb.length()-1);
                    notificationBuilder.setContentText(contentStr);
                }
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
                LogUtil.getInstance().info(LogLevel.WARN,"收到到店通通知加载图片取消");
            }
        });
    }

    public void showNotification(final Context context, final AmountStatusVo amountStatusVo){
        ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(context, 36),
                DisplayUtil.dip2px(context, 36));
        String userimage = amountStatusVo.getUserimage();
        String imageUrl  = ProtocolUtil.getAvatarUrl(context,userimage);
        NonViewAware aware = new NonViewAware(imageSize, ViewScaleType.CROP);
        ImageLoader.getInstance().displayImage(imageUrl,aware,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri,view);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri,view,failReason);
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                int status = amountStatusVo.getStatus();
                //0-待确认, 1-已拒绝, 2-已确认
                String alert = amountStatusVo.getAlert();
                String tipsMsg = null;
                if(!TextUtils.isEmpty(alert)){
                    tipsMsg = alert;
                } else if(1 == status){
                    tipsMsg = "用户已拒绝收款";
                } else {
                    tipsMsg = "用户已确认收款";
                }
                notificationBuilder.setContentTitle(tipsMsg);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                ++NOTIFY_ID;
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, AmountDetailActivity.class);
                intent.putExtra("amountStatusVo",amountStatusVo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFY_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri,view,loadedImage);
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                int status = amountStatusVo.getStatus();
                //0-待确认, 1-已拒绝, 2-已确认
                String alert = amountStatusVo.getAlert();
                String tipsMsg = null;
                if(!TextUtils.isEmpty(alert)){
                    tipsMsg = alert;
                } else if(1 == status){
                    tipsMsg = "用户已拒绝收款";
                }else {
                    tipsMsg = "用户已确认收款";
                }
                notificationBuilder.setContentTitle(tipsMsg);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setLargeIcon(loadedImage);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(loadedImage));
                }
                ++NOTIFY_ID;
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, AmountDetailActivity.class);
                intent.putExtra("amountStatusVo",amountStatusVo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFY_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri,view);
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                int status = amountStatusVo.getStatus();
                //0-待确认, 1-已拒绝, 2-已确认
                String alert = amountStatusVo.getAlert();
                String tipsMsg = null;
                if(!TextUtils.isEmpty(alert)){
                    tipsMsg = alert;
                } else if(1 == status){
                    tipsMsg = "用户已拒绝收款";
                }else {
                    tipsMsg = "用户已确认收款";
                }
                notificationBuilder.setContentTitle(tipsMsg);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                ++NOTIFY_ID;
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, AmountDetailActivity.class);
                intent.putExtra("amountStatusVo",amountStatusVo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFY_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
            }
        });
    }

    /**
     * 通知提示:客户使用邀请码成功
     * @param context
     * @param activeCodeNoticeVo
     */
    public void showNotification(final Context context, final ActiveCodeNoticeVo activeCodeNoticeVo) {
        String imageUrl = ProtocolUtil.getAvatarUrl(context,activeCodeNoticeVo.getUserimage());
        ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(context, 36),
                DisplayUtil.dip2px(context, 36));
        NonViewAware aware = new NonViewAware(imageSize, ViewScaleType.CROP);
        ImageLoader.getInstance().displayImage(imageUrl,aware,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri,view);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri,view,failReason);
                long activeTime = activeCodeNoticeVo.getCreate();
                String time = TimeUtil.getChatTime(activeTime);
                String userName = activeCodeNoticeVo.getUsername();
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                String content = context.getString(R.string.user) + " " + userName + " 于 "+ time +
                        context.getString(R.string.use_your_invite_code);
                notificationBuilder.setContentTitle("邀请码通知");
                notificationBuilder.setContentText(content);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri,view,loadedImage);
                long activeTime = activeCodeNoticeVo.getCreate();
                String time = TimeUtil.getChatTime(activeTime);
                String userName = activeCodeNoticeVo.getUsername();
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                String content = context.getString(R.string.user) + " " + userName + " 于 "+ time +
                        context.getString(R.string.use_your_invite_code);
                notificationBuilder.setContentTitle("邀请码通知");
                notificationBuilder.setContentText(content);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setLargeIcon(loadedImage);
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri,view);
                long activeTime = activeCodeNoticeVo.getCreate();
                String time = TimeUtil.getChatTime(activeTime);
                String userName = activeCodeNoticeVo.getUsername();
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                String content = context.getString(R.string.user) + " " + userName + " 于 "+ time +
                        context.getString(R.string.use_your_invite_code);
                notificationBuilder.setContentTitle("邀请码通知");
                notificationBuilder.setContentText(content);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
            }
        });
    }

    /**
     * 通知提示:客户退出当前账户
     * @param context
     */
    public void showExitAccountNotification(final Context context) {
        ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(context, 36),
                DisplayUtil.dip2px(context, 36));
        String imageUrl    = CacheUtil.getInstance().getUserPhotoUrl();
        NonViewAware aware = new NonViewAware(imageSize, ViewScaleType.CROP);
        ImageLoader.getInstance().displayImage(imageUrl,aware,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri,view);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri,view,failReason);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri,view,loadedImage);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                String content = "您的账号于" + sdf.format(new Date()) + "在另一台设备登录";
                notificationBuilder.setContentTitle("下线通知");
                notificationBuilder.setContentText(content);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setLargeIcon(loadedImage);
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri,view);
            }
        });
    }

    /**
     * 显示呼叫服务通知栏
     * @param context
     * @param alertStr
     * @param imageStr
     */
    public synchronized void showNotification(final Context context, final String alertStr,final String imageStr){
        ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(context, 36),
                DisplayUtil.dip2px(context, 36));
        String imageUrl  = ProtocolUtil.getAvatarUrl(context,imageStr);
        NonViewAware aware = new NonViewAware(imageSize, ViewScaleType.CROP);
        ImageLoader.getInstance().displayImage(imageUrl,aware,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                notificationBuilder.setContentTitle(alertStr);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
                String reason = null;
                switch (failReason.getType()) {
                    case IO_ERROR:// 文件I/O错误
                        reason = "文件I/O错误";
                        break;
                    case DECODING_ERROR:// 解码错误
                        reason = "解码错误";
                        break;
                    case NETWORK_DENIED:// 网络延迟
                        reason = "网络延迟";
                        break;
                    case OUT_OF_MEMORY:// 内存不足
                        reason = "内存不足";
                        break;
                    case UNKNOWN:// 原因不明
                        reason = "原因不明";
                        break;
                }
                LogUtil.getInstance().info(LogLevel.WARN,"收到到店通通知加载图片失败:"+reason);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                notificationBuilder.setContentTitle(alertStr);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setLargeIcon(loadedImage);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(loadedImage));
                }
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
                NotificationCompat.Builder notificationBuilder = null;
                // 1.设置显示信息
                notificationBuilder = new NotificationCompat.Builder(context);
                notificationBuilder.setContentTitle(alertStr);
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                // 2.设置点击跳转事件
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationBuilder.setContentIntent(pendingIntent);
                // 3.设置通知栏其他属性
                notificationBuilder.setAutoCancel(true);
                notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(++NOTIFY_ID, notificationBuilder.build());
            }
        });
    }

    private NotificationCompat.WearableExtender extendWear(Context context, NotificationCompat.Builder builder, EMMessage message) {
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
}
