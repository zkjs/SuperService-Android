package com.zkjinshi.superservice.listener;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.IMessageListener;
import com.zkjinshi.base.net.core.MessageReceiver;
import com.zkjinshi.base.net.core.WebSocketClient;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.base.util.VibratorHelper;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.activity.common.LoginActivity;
import com.zkjinshi.superservice.entity.MsgCustomerServiceImgChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceMediaChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceTextChat;
import com.zkjinshi.superservice.entity.MsgPushTriggerLocNotificationM2S;
import com.zkjinshi.superservice.entity.MsgUserDefine;
import com.zkjinshi.superservice.factory.MessageFactory;
import com.zkjinshi.superservice.notification.NotificationHelper;
import com.zkjinshi.superservice.request.LoginRequestManager;
import com.zkjinshi.superservice.sqlite.ChatRoomDBUtil;
import com.zkjinshi.superservice.sqlite.LatestClientDBUtil;
import com.zkjinshi.superservice.sqlite.MessageDBUtil;
import com.zkjinshi.superservice.utils.FileUtil;
import com.zkjinshi.superservice.utils.MediaPlayerUtil;
import com.zkjinshi.superservice.vo.MessageVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 客户聊天事件监听
 * 开发者：JimmyZhang
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageListener extends Handler implements IMessageListener {

    public static final String TAG = MessageReceiver.class.getSimpleName();

    public static final int RELOGIN_MSG_FLAG = 2;

    private SimpleDateFormat sdf;
    private Message notifyMessage;

    @Override
    public void onNetReceiveSucceed(String message) {
        if(TextUtils.isEmpty(message)){
            return ;
        }
        Gson gson = null;
        try {
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");

            if (type == ProtocolMSG.MSG_ServerRepeatLogin) {//重复登录
                WebSocketManager.getInstance().logoutIM(ServiceApplication.getContext());
                notifyMessage = new Message();
                notifyMessage.what = RELOGIN_MSG_FLAG;
                sendMessage(notifyMessage);
                return;
            }

            /** 文本消息处理 */
            if (ProtocolMSG.MSG_CustomerServiceTextChat == type) {
                if(gson == null){
                    gson = new Gson();
                }
                LogUtil.getInstance().info(LogLevel.INFO, "接收到文本消息:");
                MsgCustomerServiceTextChat msgText = gson.fromJson(message,
                                         MsgCustomerServiceTextChat.class);
                /** 处理消息入库 （数据库相关操作） */
                long resultCount = MessageDBUtil.getInstance().addMessage(msgText);
                if(resultCount > 0){
                    MessageVo messageVo = MessageFactory.getInstance().buildMessageVoByMsgText(msgText);
                    String sessionId = messageVo.getSessionId();
                    if(!TextUtils.isEmpty(sessionId)) {
                        if(ChatRoomDBUtil.getInstance().isChatRoomExistsBySessionID(sessionId)){
                            ChatRoomDBUtil.getInstance().addChatRoom(messageVo);
                        } else {
                            ChatRoomDBUtil.getInstance().updateChatRoom(messageVo);
                        }
                    }
                    NotificationHelper.getInstance().showNotification(ServiceApplication.getContext(), messageVo);
                }
            }

            /** 音频消息处理 */
            if(ProtocolMSG.MSG_CustomerServiceMediaChat == type){
                if(gson == null)
                    gson = new Gson();
                LogUtil.getInstance().info(LogLevel.INFO, "接收到音频消息:");
                MsgCustomerServiceMediaChat msgMediaChat = gson.fromJson(
                              message, MsgCustomerServiceMediaChat.class);

                /** 保存音频对象，并获得路径 */
                String mediaName = msgMediaChat.getFilename();
                String audioPath = FileUtil.getInstance().getAudioPath() + mediaName;
                /** 消息表中添加音频消息记录 */
                long resultCount = MessageDBUtil.getInstance().addMessage(msgMediaChat);
                if(resultCount > 0){
                    MessageVo messageVO = MessageFactory.getInstance().buildMessageVoByMsgMedia(msgMediaChat, audioPath);
                    /** insert or update into table ChatRoom */
                    String sessionId = messageVO.getSessionId();
                    if(!TextUtils.isEmpty(sessionId)) {
                        if(ChatRoomDBUtil.getInstance().isChatRoomExistsBySessionID(sessionId)){
                            ChatRoomDBUtil.getInstance().addChatRoom(messageVO);
                        } else {
                            ChatRoomDBUtil.getInstance().updateChatRoom(messageVO);
                        }
                    }
                    NotificationHelper.getInstance().showNotification(ServiceApplication.getContext(), messageVO);
                }
            }

            /** 图片消息处理 */
            if(ProtocolMSG.MSG_CustomerServiceImgChat == type) {
                if(gson == null)
                    gson = new Gson();
                LogUtil.getInstance().info(LogLevel.INFO, "接收到图片消息:");
                MsgCustomerServiceImgChat msgImgChat = gson.fromJson(message,
                                            MsgCustomerServiceImgChat.class);
                /** 消息表中添加图片消息记录 */
                long resultCount = MessageDBUtil.getInstance().addMessage(msgImgChat);
                if(resultCount > 0){
                    MessageVo imageMessageVo = MessageFactory.getInstance().buildMessageVoByMsgImg(
                            msgImgChat);
                    String sessionId = imageMessageVo.getSessionId();
                    if(!TextUtils.isEmpty(sessionId)) {
                        if(ChatRoomDBUtil.getInstance().isChatRoomExistsBySessionID(sessionId)){
                            ChatRoomDBUtil.getInstance().addChatRoom(imageMessageVo);
                        } else {
                            ChatRoomDBUtil.getInstance().updateChatRoom(imageMessageVo);
                        }
                    }
                    NotificationHelper.getInstance().showNotification(ServiceApplication.getContext(), imageMessageVo);
                }
            }

            /** 用户到店通知 */
            if (ProtocolMSG.MSG_PushTriggerLocNotification_M2S == type) {
                if(gson == null){
                    gson = new Gson();
                }
                LogUtil.getInstance().info(LogLevel.INFO, "用户到店通知");
                MediaPlayerUtil.playNotifyVoice(ServiceApplication.getContext());
                VibratorHelper.vibratorShark(ServiceApplication.getContext());
                MsgPushTriggerLocNotificationM2S msgLocNotification = gson.fromJson(message,
                                                    MsgPushTriggerLocNotificationM2S.class);
                //对到店通知进行数据库插入操作
                long addResult = LatestClientDBUtil.getInstance().addLatestClient(msgLocNotification);
                if(addResult > 0){
                    LogUtil.getInstance().info(LogLevel.INFO, "添加到店用户成功:"+ msgLocNotification.toString());
                } else {
                    LogUtil.getInstance().info(LogLevel.INFO, "添加到店用户失败");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(Constants.ZKJINSHI_BASE_TAG, TAG + ".onNetReceiveSucceed()->message:" + message);
    }

    @Override
    public void onWebsocketConnected(WebSocketClient webSocketClient) {
        //发送登录包根据自己的业务逻辑实现，以下代码仅供参考
        LoginRequestManager.getInstance().init().sendLoginRequest(webSocketClient);
    }

    /**
     * 显示重复登录提示框
     *
     * @param context
     */
    private synchronized void showReLoginDialog(final Context context) {
        Dialog dialog = null;
        sdf = new SimpleDateFormat("HH:mm");
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("下线通知");
        customBuilder.setMessage("您的账号于" + sdf.format(new Date()) + "在另一台设备登录");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        customBuilder.setPositiveButton("重新登录", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                WebSocketManager.getInstance().initClient();
            }
        });
        dialog = customBuilder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case RELOGIN_MSG_FLAG:
                showReLoginDialog(ServiceApplication.getContext());
                break;

            default:
        }
        super.handleMessage(msg);
    }
}
