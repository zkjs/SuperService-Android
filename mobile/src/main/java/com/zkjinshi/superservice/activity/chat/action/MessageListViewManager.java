package com.zkjinshi.superservice.activity.chat.action;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.net.queue.QueueType;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ChatAdapter;
import com.zkjinshi.superservice.entity.MsgCustomerServiceChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceImgChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceImgChatRSP;
import com.zkjinshi.superservice.entity.MsgCustomerServiceMediaChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceMediaChatRSP;
import com.zkjinshi.superservice.entity.MsgCustomerServiceTextChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceTextChatRSP;
import com.zkjinshi.superservice.entity.MsgShopDisband;
import com.zkjinshi.superservice.entity.MsgShopDisbandRSP;
import com.zkjinshi.superservice.factory.MessageFactory;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.request.MsgShopDisbandSessionTool;
import com.zkjinshi.superservice.sqlite.MessageDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.UUIDBuilder;
import com.zkjinshi.superservice.view.MsgListView;
import com.zkjinshi.superservice.vo.MessageVo;
import com.zkjinshi.superservice.vo.MimeType;
import com.zkjinshi.superservice.vo.SendStatus;
import com.zkjinshi.superservice.vo.TxtExtType;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息ListView管理器
 * 开发者：vincent
 * 日期：2015/8/1
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageListViewManager extends Handler implements MsgListView.IXListViewListener,
        ChatAdapter.ResendListener, EMEventListener {

    private static final String TAG = MessageListViewManager.class.getSimpleName();
    private static final int MESSAGE_LIST_VIEW_UPDATE_UI = 0X00;

    private Context context;
    private MsgListView messageListView;
    private ChatAdapter chatAdapter;
    private EMConversation conversation;
    private Vector<String> messageVector = new Vector<String>();//存储页面发送的消息ID
    private List<EMMessage> currentMessageList = new ArrayList<EMMessage>();
    private ArrayList<EMMessage> requestMessageList;
    private static final int PRE_LOAD_PAGE_SIZE = 20;// 每次预加载20条记录
    private String userId;

    private LinkedBlockingQueue<MessageVo> messageQueue = new LinkedBlockingQueue<MessageVo>();

    public MessageListViewManager(Context context, String userId) {
        this.context    = context;
        this.userId = userId;
    }

    public void init() {
        initView((Activity) context);
        initData();
        initListeners();
    }

    private void initView(Activity activity) {
        messageListView = (MsgListView) activity
                .findViewById(R.id.msg_listView);
    }

    private void initData() {
        conversation = EMChatManager.getInstance().getConversation(userId);
        clearChatRoomBadgeNum();
        setOverScrollMode(messageListView);
        messageQueue = new LinkedBlockingQueue<MessageVo>();
        chatAdapter = new ChatAdapter(context, null);
        chatAdapter.setResendListener(this);
        messageListView.setPullLoadEnable(false);
        messageListView.setAdapter(chatAdapter);
        if (!chatAdapter.isEmpty()) {
            messageListView.setSelection(chatAdapter.getCount() - 1);
        }
        currentMessageList = conversation.getAllMessages();
        chatAdapter.setMessageList(currentMessageList);
        scrollBottom();
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventDeliveryAck,
                        EMNotifierEvent.Event.EventReadAck});
    }

    private void initListeners() {
        messageListView.setXListViewListener(this);
    }

    public synchronized void destoryMessageListViewManager() {
        messageVector.clear();
        clearChatRoomBadgeNum();
        EMChatManager.getInstance().unregisterEventListener(this);
    }

    /**
     * 将消息设置为已读
     */
    private void clearChatRoomBadgeNum(){
        conversation.markAllMessagesAsRead();
    }

    /**
     * 发送默认文本消息
     * @param content
     */
    public void sendTextMessage(String content) {
        EMMessage message = EMMessage.createTxtSendMessage(content, userId);
        message.setAttribute(Constants.MSG_TXT_EXT_TYPE, TxtExtType.DEFAULT.getVlaue());
        sendMessage(message);
    }

    /**
     * 发送默认卡片消息
     * @param content
     */
    public void sendCardMessage(String content){
        EMMessage message = EMMessage.createTxtSendMessage(content, userId);
        message.setAttribute(Constants.MSG_TXT_EXT_TYPE,TxtExtType.CARD.getVlaue());
        sendMessage(message);
    }

    /**
     * 发送语音消息
     * @param filePath
     * @param voiceTime
     */
    public void sendVoiceMessage(String filePath, int voiceTime) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, voiceTime, userId);
        sendMessage(message);
    }

    /**
     * 发送图片消息
     * @param filePath
     */
    public void sendImageMessage(String filePath) {
        EMMessage message = EMMessage.createImageSendMessage(filePath, false, userId);
        sendMessage(message);
    }

    protected void sendMessage(final EMMessage message){
        message.setChatType(EMMessage.ChatType.Chat);
        message.status = EMMessage.Status.INPROGRESS;
        currentMessageList.add(message);
        chatAdapter.setMessageList(currentMessageList);
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentMessageList.contains(message)) {
                            message.status = EMMessage.Status.SUCCESS;
                            chatAdapter.setMessageList(currentMessageList);
                            scrollBottom();
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    @Override
    public void onRefresh() {
        if (null == currentMessageList) {
            currentMessageList = new ArrayList<>();
        }
        currentMessageList = conversation.getAllMessages();
        if(null != chatAdapter){
            chatAdapter.setMessageList(currentMessageList);
        }
    }

    @Override
    public void onLoadMore() {

    }

    /**
     * 重发消息
     * @param messageVo
     */
    @Override
    public void onResend(EMMessage messageVo) {
        //消息重发送处理
        //1.删除消息条目
        //2.更新消息条目并将消息状态置为发送中

    }

    public MsgListView getMessageListView() {
        return messageListView;
    }

    /**
     * 设置滚动位置
     */
    public void scrollBottom() {
        if (currentMessageList != null && !currentMessageList.isEmpty()) {
            if (currentMessageList != null && currentMessageList.size() > 0) {
                messageListView.requestFocusFromTouch();
                messageListView.setSelection(currentMessageList.size() - 1);
            }
        }
    }

    /**
     * 禁止ListView自动滚动
     * @param listView
     */
    @SuppressLint("NewApi")
    public static void setOverScrollMode(ListView listView) {
        try {
            int sdk = DeviceUtils.getSdk();
            if (sdk > 10) {
                listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            } else {
                Method method = AbsListView.class.getMethod(
                        "setOverScrollMode", int.class);
                method.invoke(listView, View.OVER_SCROLL_NEVER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitle(TextView titleTv){
        if(null != titleTv){
            if(!TextUtils.isEmpty(userId)){
                titleTv.setText(userId);
            }
            if(null != conversation){
                if(conversation.getIsGroup()){
                    EMGroup group = EMGroupManager.getInstance().getGroup(userId);
                    if (group != null){
                        titleTv.setText(group.getGroupName());
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
                // 获取到message
                EMMessage message = (EMMessage) event.getData();
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(userId)) {
                    clearChatRoomBadgeNum();
                    Message msg = Message.obtain();
                    msg.what = MESSAGE_LIST_VIEW_UPDATE_UI;
                    sendMessage(msg);
                    // 声音和震动提示有新消息
                } else {
                    // 如果消息不是和当前聊天ID的消息
                }
                break;
            case EventDeliveryAck:
            case EventReadAck:
                // 获取到message
                break;
            case EventOfflineMessage:
                // a list of offline messages
                // List<EMMessage> offlineMessages = (List<EMMessage>)
                // event.getData();
                break;
            default:
                break;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MESSAGE_LIST_VIEW_UPDATE_UI:
                chatAdapter.setMessageList(currentMessageList);
                scrollBottom();
                break;
        }
    }
}