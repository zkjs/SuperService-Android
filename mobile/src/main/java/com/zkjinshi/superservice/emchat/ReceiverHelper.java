package com.zkjinshi.superservice.emchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

/**
 * 消息广播接收帮助类
 * 开发者：JimmyZhang
 * 日期：2015/11/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ReceiverHelper {

    private ReceiverHelper(){}

    private static ReceiverHelper instance;

    private Context context;

    public synchronized static ReceiverHelper getInstance(){
        if(null == instance){
            instance = new ReceiverHelper();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
    }

    /**
     * 1、注册消息发送成功广播
     */
    public void regiserSuccMessageReceiver(){
        //EMChatManager.getInstance().getChatOptions().setRequireDeliveryAck(flag)
        //如果用到已发送的回执需要把这个flag设置成true
        IntentFilter deliveryAckMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getDeliveryAckMessageBroadcastAction());
        deliveryAckMessageIntentFilter.setPriority(5);
        context.registerReceiver(deliveryAckMessageReceiver, deliveryAckMessageIntentFilter);
        EMChat.getInstance().setAppInited();
    }

    /**
     * 消息送达BroadcastReceiver
     */
    private BroadcastReceiver deliveryAckMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");
            EMConversation conversation = EMChatManager.getInstance().getConversation(from);
            if (conversation != null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);
                if (msg != null) {

                }
            }
        }
    };

    /**
     * 2、注册ack回执消息广播
     *
     */
    public void regiserAckMessageReceiver(){
        //EMChatManager.getInstance().getChatOptions().setRequireAck(flag);
        //如果用到已读的回执需要把这个flag设置成true
        IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(3);
        context.registerReceiver(ackMessageReceiver, ackMessageIntentFilter);
        EMChat.getInstance().setAppInited();
    }

    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");
            EMConversation conversation = EMChatManager.getInstance().getConversation(from);
            if (conversation != null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);
                if (msg != null) {
                    msg.isAcked = true;
                }
            }
        }
    };

    /**
     *3、注册新消息广播
     *
     */
    public void regiserNewMessageReceiver(){
        //只有注册了广播才能接收到新消息，目前离线消息，在线消息都是走接收消息的广播（离线消息目前无法监听，在登录以后，接收消息广播会执行一次拿到所有的离线消息）
        NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        context.registerReceiver(msgReceiver, intentFilter);
        EMChat.getInstance().setAppInited();
    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 注销广播
            abortBroadcast();
            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");
            //发送方
            String username = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            EMConversation conversation = EMChatManager.getInstance().getConversation(username);
            // 如果是群聊消息，获取到group id
            if (message.getChatType() == EMMessage.ChatType.Chat) {
                username = message.getTo();
            }
            if (!username.equals(username)) {
                // 消息不是发给当前会话
                return;
            }
        }
    }

}
