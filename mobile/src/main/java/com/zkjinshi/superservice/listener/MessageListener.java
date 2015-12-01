package com.zkjinshi.superservice.listener;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.IMessageListener;
import com.zkjinshi.base.net.core.MessageReceiver;
import com.zkjinshi.base.net.core.WebSocketClient;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.entity.MsgCustomerServiceImgChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceMediaChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceTextChat;
import com.zkjinshi.superservice.entity.MsgOfflineMessageRSP;
import com.zkjinshi.superservice.entity.MsgPushTriggerLocNotificationM2S;
import com.zkjinshi.superservice.factory.MessageFactory;
import com.zkjinshi.superservice.notification.NotificationHelper;
import com.zkjinshi.superservice.request.LoginRequestManager;
import com.zkjinshi.superservice.sqlite.ChatRoomDBUtil;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.sqlite.MessageDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.FileUtil;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.ContactType;
import com.zkjinshi.superservice.vo.MessageVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

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

            if (type == ProtocolMSG.MSG_ClientLogin_RSP) {
                return;
            }

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
                ChatRoomDBUtil.getInstance().addChatRoom(msgText);
                if(resultCount > 0){
                    MessageVo messageVo = MessageFactory.getInstance().buildMessageVoByMsgText(msgText);
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
                ChatRoomDBUtil.getInstance().addChatRoom(msgMediaChat);
                if(resultCount > 0){
                    MessageVo messageVO = MessageFactory.getInstance().buildMessageVoByMsgMedia(msgMediaChat, audioPath);
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
                ChatRoomDBUtil.getInstance().addChatRoom(msgImgChat);
                if(resultCount > 0){
                    MessageVo imageMessageVo = MessageFactory.getInstance().buildMessageVoByMsgImg(
                            msgImgChat);
                    NotificationHelper.getInstance().showNotification(ServiceApplication.getContext(), imageMessageVo);
                }
            }

            /** 用户到店通知 */
            if (ProtocolMSG.MSG_PushTriggerLocNotification_M2S == type) {
                if(gson == null){
                    gson = new Gson();
                }
                LogUtil.getInstance().info(LogLevel.INFO, "用户到店通知");
                String roleID    = CacheUtil.getInstance().getRoleID();
                if(!TextUtils.isEmpty(roleID)){

                    MsgPushTriggerLocNotificationM2S msgLocNotification = gson.fromJson(message,
                                                        MsgPushTriggerLocNotificationM2S.class);
                    int intRoleID = Integer.parseInt(roleID);

                    //判断当前用户角色类型 1是管理员, 2是销售,3 其他前台什么的
                    if(intRoleID == 2){
                        String   clientID = msgLocNotification.getUserid();
                        ClientVo clientVo = ClientDBUtil.getInstance().queryClientByClientID(clientID);
                        if(null != clientVo && clientVo.getContactType() == ContactType.NORMAL){
                            NotificationHelper.getInstance().showNotification(ServiceApplication.getContext(), msgLocNotification);
                        }
                    }else{
                        NotificationHelper.getInstance().showNotification(ServiceApplication.getContext(),
                                                                                      msgLocNotification);
                    }
                }
            }

            /** 获取用户离线消息 */
            if (ProtocolMSG.MSG_OfflineMssage_RSP == type) {
                if(gson == null){
                    gson = new Gson();
                }
                MsgOfflineMessageRSP msgOfflineMessageRSP = gson.fromJson(message, MsgOfflineMessageRSP.class);
                LogUtil.getInstance().info(LogLevel.INFO, "获取用户离线消息response数" + msgOfflineMessageRSP.getOffmsgnum());
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

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case RELOGIN_MSG_FLAG:
                Log.i(TAG, "多设备通账号登录异常");
                break;

            default:
        }
        super.handleMessage(msg);
    }
}
