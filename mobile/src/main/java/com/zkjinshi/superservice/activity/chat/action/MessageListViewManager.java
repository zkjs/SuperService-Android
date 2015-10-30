package com.zkjinshi.superservice.activity.chat.action;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

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
import com.zkjinshi.superservice.entity.MsgShopDisbandSession;
import com.zkjinshi.superservice.entity.MsgShopDisbandSessionRSP;
import com.zkjinshi.superservice.factory.MessageFactory;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.request.MsgShopDisbandSessionTool;
import com.zkjinshi.superservice.sqlite.ChatRoomDBUtil;
import com.zkjinshi.superservice.sqlite.MessageDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.UUIDBuilder;
import com.zkjinshi.superservice.view.MsgListView;
import com.zkjinshi.superservice.vo.MessageVo;
import com.zkjinshi.superservice.vo.MimeType;
import com.zkjinshi.superservice.vo.SendStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * 消息ListView管理器
 * 开发者：vincent
 * 日期：2015/8/1
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageListViewManager implements MsgListView.IXListViewListener,
        ChatAdapter.ResendListener, IMessageObserver {

    private static final String TAG = "MessageListViewManager";

    private Context              context;
    private MsgListView          messageListView;
    private ChatAdapter          chatAdapter;
    private Vector<String>       messageVector      = new Vector<>();//存储页面发送的消息ID
    private List<MessageVo> currentMessageList = new ArrayList<>();
    private ArrayList<MessageVo> requestMessageList;
    private long lastSendTime;
    private static final int PRE_LOAD_PAGE_SIZE = 20;// 每次预加载20条记录

    private MessageVo       mMessageVo;
    private String          mShopID;
    private String          mSessionID;
    private String          mClientID;

    public MessageListViewManager(Context context, String shopID, String sessionId) {
        this.context    = context;
        this.mShopID    = shopID;
        this.mSessionID = sessionId;
    }

    public MessageListViewManager(Context context, String shopID, String sessionId, String clientID) {
        this.context    = context;
        this.mShopID    = shopID;
        this.mSessionID = sessionId;
        this.mClientID  = clientID;
    }

    public void init() {
        initView((Activity) context);
        initData();
        initListeners();
    }

    private void initView(Activity activity) {
        messageListView = (MsgListView) activity.findViewById(R.id.msg_listView);
    }

    private void initData() {
        addObservers();
        clearChatRoomBadgeNum(mSessionID);

        setOverScrollMode(messageListView);
        chatAdapter = new ChatAdapter(context,null);
        chatAdapter.setResendListener(this);
        messageListView.setPullLoadEnable(false);
        messageListView.setAdapter(chatAdapter);
        if (!chatAdapter.isEmpty()) {
            messageListView.setSelection(chatAdapter.getCount() - 1);
        }

        //获得本地最后一次发送消息的时间
        lastSendTime = MessageDBUtil.getInstance().queryLastSendTime(mSessionID);
        // 查询数据库获得第一页数据
        queryPageMessages(mSessionID, mShopID, mClientID, PRE_LOAD_PAGE_SIZE, lastSendTime, true);
    }

    private void initListeners() {
        messageListView.setXListViewListener(this);
    }

    public synchronized void destoryMessageListViewManager() {
        removeObservers();
        messageVector.clear();
        clearChatRoomBadgeNum(mSessionID);
    }

    /**
     * 将消息设置为已读
     * @param sessionId
     */
    private void clearChatRoomBadgeNum(String sessionId){
        long updateMsgReaded = MessageDBUtil.getInstance().updateMsgReadedBySessionID(sessionId);
        LogUtil.getInstance().info(LogLevel.INFO, "更新已读消息条数:" + updateMsgReaded);
        LogUtil.getInstance().info(LogLevel.INFO, "sessionID:" + sessionId + "--更新已读消息条数:" + updateMsgReaded);
        //TODO Jimmy  2、网络请求聊天室置为已读消息
    }

    /**
     * 添加EventBus消息通知观察者
     */
    private void addObservers() {
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceTextChat_RSP);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceMediaChat_RSP);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceImgChat_RSP);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceTextChat);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceMediaChat);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceImgChat);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_ShopDisbandSession_RSP);
    }

    /**
     * 删除EventBus消息通知观察者
     */
    private void removeObservers() {
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceTextChat_RSP);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceMediaChat_RSP);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceImgChat_RSP);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceTextChat);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceMediaChat);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceImgChat);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_ShopDisbandSession_RSP);
    }

    /**
     * 解散回话
     */
    public void sendDisableSession(){
        MsgShopDisbandSession msgShopDisbandSession = MsgShopDisbandSessionTool.buildMsgShopDisbandSession(mShopID,mSessionID);
        if(null != msgShopDisbandSession){
            Gson gson = new Gson();
            String jsonStr = gson.toJson(msgShopDisbandSession);
            WebSocketManager.getInstance().sendMessage(jsonStr, QueueType.FIRST);
        }
    }

    /**
     * 发送文本消息
     * @param content
     */
    public void sendTextMessage(String content) {
        String tempMessageId    = UUIDBuilder.getInstance().getRandomUUID();
        String defaultRuleType  = context.getString(R.string.default_rule_type);

        long tempSendTime    = System.currentTimeMillis();
        messageVector.add(tempMessageId);

        /** 1、IM发送文本消息 */
        mMessageVo = buildTextMessageVo(mShopID, mSessionID,
                content, tempMessageId, tempSendTime,
                SendStatus.SENDING, defaultRuleType);

        /** 2、保存文本消息到sqlite(注意此时的消息正在发送中) */
        MessageDBUtil.getInstance().addMessage(mMessageVo);

        /** 3、构建文本vo实体，将消息内容显示到页面 */
        currentMessageList.add(mMessageVo);
        chatAdapter.setData(currentMessageList);
        scrollBottom();
        sendMessageVo(mMessageVo);
    }

    /**
     * 发送文本消息
     * @param content
     */
    public void sendTextMessage(String shopID, String content, String ruleType) {
        //step3
        String tempMessageId = UUIDBuilder.getInstance().getRandomUUID();
        long tempSendTime = System.currentTimeMillis();
        messageVector.add(tempMessageId);
        /** 1、IM发送文本消息 */
        mMessageVo = buildTextMessageVo(shopID, mSessionID,
                content, tempMessageId, tempSendTime, SendStatus.SENDING, ruleType);

        /** 2、保存文本消息到sqlite(注意此时的消息正在发送中) */
        MessageDBUtil.getInstance().addMessage(mMessageVo);

        /** 3、构建文本vo实体，将消息内容显示到页面 */
        currentMessageList.add(mMessageVo);
        chatAdapter.setData(currentMessageList);
        scrollBottom();
        sendMessageVo(mMessageVo);
    }

    /**
     * 发送预定文本消息
     * @param content
     */
    public void sendBookTextMessage(String content) {
        String tempMessageId = UUIDBuilder.getInstance().getRandomUUID();
        String defaultRuleType = context.getString(R.string.default_rule_type);

        long tempSendTime = System.currentTimeMillis();
        messageVector.add(tempMessageId);
        /** 1、IM发送预定文本消息 */
        mMessageVo = buildBookTextMessageVo(mShopID, mSessionID, content,
                tempMessageId, tempSendTime,
                SendStatus.SENDING, defaultRuleType);

        /** 2、保存文本消息到sqlite(注意此时的消息正在发送中) */
        MessageDBUtil.getInstance().addMessage(mMessageVo);

        /** 3、构建文本vo实体，将消息内容显示到页面 */
        currentMessageList.add(mMessageVo);
        chatAdapter.setData(currentMessageList);
        scrollBottom();
        sendMessageVo(mMessageVo);
    }

    /**
     * 发送语音消息
     * @param fileName
     * @param filePath
     * @param voiceTime
     */
    public void sendAudioMessage( String shopID, String fileName,
                                  final String filePath, int voiceTime, String ruleType) {
        //临时消息ID
        String tempMessageId = UUIDBuilder.getInstance().getRandomUUID();
        long tempSendTime    = System.currentTimeMillis();
        messageVector.add(tempMessageId);

        String attachId = UUIDBuilder.getInstance().getRandomUUID();

        mMessageVo = buildAudioMessageVo(shopID, mSessionID, tempMessageId,
                tempSendTime, SendStatus.SENDING,
                attachId, fileName, filePath,
                voiceTime, ruleType);

        // 1、保存文本消息到sqlite(注意此时的消息正在发送中)
        MessageDBUtil.getInstance().addMessage(mMessageVo);

        // 3、构建语音vo实体，将消息内容显示到页面
        currentMessageList.add(mMessageVo);
        chatAdapter.setData(currentMessageList);
        scrollBottom();
        sendMessageVo(mMessageVo);
    }

    /**
     * 发送图片消息
     * @param fileName
     * @param filePath
     */
    public void sendImageMessage(String shopID, String fileName,final String filePath ,String ruleType) {
        //临时消息ID
        String tempMessageId = UUIDBuilder.getInstance().getRandomUUID();
        long tempSendTime = System.currentTimeMillis();
        messageVector.add(tempMessageId);
        String attachId = UUIDBuilder.getInstance().getRandomUUID();

        /** 生成MessageVo对象 */
        mMessageVo = buildImageMessageVo(
                shopID, mSessionID, tempMessageId,
                tempSendTime, SendStatus.SENDING,
                attachId, fileName, filePath, ruleType);

        /** 1 保存文本消息到sqlite(注意此时的消息正在发送中) */
        MessageDBUtil.getInstance().addMessage(mMessageVo);

        /** 2 构建图片vo实体，将消息内容显示到页面 */
        currentMessageList.add(mMessageVo);
        chatAdapter.setData(currentMessageList);
        scrollBottom();
        sendMessageVo(mMessageVo);
    }

    /**
     * 向服务器发送消息对象
     * @param messageUiVo
     */
    private void sendMessageVo(final MessageVo messageUiVo) {
        messageUiVo.setSessionId(mSessionID);
        switch (messageUiVo.getMimeType()){
            case TEXT://文本
            case CARD://卡片
                final MsgCustomerServiceTextChat msgText = MessageFactory.getInstance().
                        buildMsgTextByMessageVo(messageUiVo);
                Gson gson = new Gson();
                String textJson = gson.toJson(msgText);
                WebSocketManager.getInstance().sendMessage(textJson);
                break;
            case AUDIO://语音
                final MsgCustomerServiceMediaChat msgMedia = MessageFactory.getInstance().
                        buildMsgMediaByMessageVo(messageUiVo);
                HashMap<String,String> voiceBizMap = new HashMap<String,String>();
                HashMap<String,String> voiceFileMap = new HashMap<String,String>();
                voiceBizMap.put("SessionID", msgMedia.getSessionid());
                voiceBizMap.put("FromID", msgMedia.getFromid());
                voiceBizMap.put("ShopID", msgMedia.getShopid());
                voiceBizMap.put("Format", "aac");
                voiceBizMap.put("TempID", msgMedia.getTempid());
                voiceFileMap.put("Body", messageUiVo.getFilePath());
                NetRequest voiceRequest = new NetRequest(ConfigUtil.getInst().getMediaDomain()+"media/upload");
                voiceRequest.setBizParamMap(voiceBizMap);
                voiceRequest.setFileParamMap(voiceFileMap);
                NetRequestTask voiceRequestTask = new NetRequestTask(context,voiceRequest, NetResponse.class);
                voiceRequestTask.setNetRequestListener(new NetRequestListener() {
                    @Override
                    public void onNetworkRequestError(int errorCode, String errorMessage) {
                        Log.i(TAG, "errorCode:" + errorCode);
                        Log.i(TAG, "errorMessage:" + errorMessage);
                    }

                    @Override
                    public void onNetworkRequestCancelled() {

                    }

                    @Override
                    public void onNetworkResponseSucceed(NetResponse result) {
                        Log.i(TAG, "rawResult:" + result.rawResult);
                        try {
                            JSONObject responseObject = new JSONObject(result.rawResult);
                            int resultCode = (int) responseObject.get("result");
                            // 文件上传成功
                            if (Constants.POST_SUCCESS == resultCode) {
                                String mediaUrl = (String) responseObject.get("url");
                                messageUiVo.setUrl(mediaUrl);
                                //更新数据库信息
                                MessageDBUtil.getInstance().updateMessage(MessageFactory.getInstance().
                                        buildContentValues(messageUiVo));
                                //获得待发送协议消息
                                MsgCustomerServiceMediaChat msgMedia = MessageFactory.getInstance().
                                        buildMsgMediaByMessageVo(messageUiVo);
                                Gson gson = new Gson();
                                String mediaJson = gson.toJson(msgMedia);
                                LogUtil.getInstance().info(LogLevel.INFO, "mediaJson:" + mediaJson);
                                WebSocketManager.getInstance().sendMessage(mediaJson);
                            } else {
                                Gson gson = new Gson();
                                String mediaJson = gson.toJson(msgMedia);
                                LogUtil.getInstance().info(LogLevel.INFO, "mediaJson:" + mediaJson);
                                WebSocketManager.getInstance().sendMessage(mediaJson);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void beforeNetworkRequestStart() {

                    }
                });
                voiceRequestTask.execute();

                break;
            case IMAGE://图片
                final MsgCustomerServiceImgChat msgImage = MessageFactory.getInstance().
                        buildMsgImgByMessageVo(messageUiVo);
                HashMap<String,String> imgBizMap = new HashMap<String,String>();
                HashMap<String,String> imgFileMap = new HashMap<String,String>();
                imgBizMap.put("SessionID",msgImage.getSessionid());
                imgBizMap.put("FromID",msgImage.getFromid());
                imgBizMap.put("ShopID", msgImage.getShopid());
                imgBizMap.put("Format", msgImage.getFormat());
                imgBizMap.put("TempID",msgImage.getTempid());
                imgFileMap.put("Body", msgImage.getFilePath());
                NetRequest uploadImgRequest = new NetRequest(ConfigUtil.getInst().getMediaDomain()+"img/upload");
                uploadImgRequest.setBizParamMap(imgBizMap);
                uploadImgRequest.setFileParamMap(imgFileMap);
                NetRequestTask uploadImgRequestTask = new NetRequestTask(context,uploadImgRequest, NetResponse.class);
                uploadImgRequestTask.setNetRequestListener(new NetRequestListener() {
                    @Override
                    public void onNetworkRequestError(int errorCode, String errorMessage) {
                        Log.i(TAG, "errorCode:" + errorCode);
                        Log.i(TAG, "errorMessage:" + errorMessage);
                    }

                    @Override
                    public void onNetworkRequestCancelled() {

                    }

                    @Override
                    public void onNetworkResponseSucceed(NetResponse result) {
                        Log.i(TAG, "rawResult:" + result.rawResult);
                        try {
                            JSONObject responseObject = new JSONObject(result.rawResult);
                            int resultCode = (int) responseObject.get("result");
                            // 文件上传成功
                            if (Constants.POST_SUCCESS == resultCode) {
                                String imageUrl = (String) responseObject.get("url");//正常图的URL
                                String scaleUrl = (String) responseObject.get("s_url");//缩略图的URL
                                messageUiVo.setUrl(imageUrl);
                                messageUiVo.setScaleUrl(scaleUrl);
                                msgImage.setUrl(imageUrl);
                                msgImage.setScaleurl(scaleUrl);
                                // 更新数据库url地址
                                MessageDBUtil.getInstance().updateMessage(MessageFactory.getInstance().
                                        buildContentValues(messageUiVo));
                            }
                            //获得待发送协议消息
                            Gson gson = new Gson();
                            String imageJson = gson.toJson(msgImage);
                            LogUtil.getInstance().info(LogLevel.INFO, "imageJson:" + imageJson);
                            WebSocketManager.getInstance().sendMessage(imageJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void beforeNetworkRequestStart() {

                    }
                });
                uploadImgRequestTask.execute();
                break;
            case VIDEO://视频
                break;
            case APPLICATION://文件
                break;
        }
    }

    public MessageVo buildBookTextMessageVo(String shopID, String sessionId, String content,
                                            String tempMessageId, long sendTime,
                                            SendStatus sendStatus, String ruleType) {
        MessageVo messageVo = new MessageVo();
        messageVo.setMessageId(tempMessageId);//未发送前的
        messageVo.setSessionId(sessionId);
        messageVo.setContent(content);
        messageVo.setTempId(tempMessageId);
        messageVo.setContactId(CacheUtil.getInstance().getUserId());
        messageVo.setContactName(CacheUtil.getInstance().getUserName());
        messageVo.setShopId(shopID);
        messageVo.setSendStatus(sendStatus);
        messageVo.setSendTime(sendTime);
        messageVo.setMimeType(MimeType.CARD);
        messageVo.setRuleType(ruleType);
        messageVo.setIsRead(true);
        return messageVo;
    }

    public MessageVo buildTextMessageVo(
            String shopID, String sessionId, String content,
            String tempMessageId, long sendTime,
            SendStatus sendStatus, String ruleType) {
        MessageVo messageVo = new MessageVo();
        messageVo.setMessageId(tempMessageId);//未发送前的
        messageVo.setSessionId(sessionId);
        messageVo.setContent(content);
        messageVo.setTempId(tempMessageId);
        messageVo.setContactId(CacheUtil.getInstance().getUserId());
        messageVo.setContactName(CacheUtil.getInstance().getUserName());
        messageVo.setShopId(shopID);
        messageVo.setSendStatus(sendStatus);
        messageVo.setSendTime(sendTime);
        messageVo.setMimeType(MimeType.TEXT);
        messageVo.setRuleType(ruleType);
        messageVo.setIsRead(true);
        return messageVo;
    }

    public MessageVo buildImageMessageVo(
            String shopID, String sessionId, String tempMessageId,
            long sendTime, SendStatus sendStatus,
            String attachId, String fileName,
            String filePath, String ruleType) {
        MessageVo messageVo = new MessageVo();
        messageVo.setShopId(shopID);
        messageVo.setContactId(CacheUtil.getInstance().getUserId());
        messageVo.setContactName(CacheUtil.getInstance().getUserName());
        messageVo.setMessageId(tempMessageId);
        messageVo.setSessionId(sessionId);
        messageVo.setTempId(tempMessageId);
        messageVo.setSendStatus(sendStatus);
        messageVo.setSendTime(sendTime);
        messageVo.setMimeType(MimeType.IMAGE);
        messageVo.setAttachId(attachId);
        messageVo.setFileName(fileName);
        messageVo.setFilePath(filePath);
        messageVo.setRuleType(ruleType);
        messageVo.setIsRead(true);
        return messageVo;
    }

    public MessageVo buildAudioMessageVo(
            String shopID, String sessionId,
            String tempMessageId, long sendTime,
            SendStatus sendStatus, String attachId,
            String fileName, String filePath,
            int voiceTime, String ruleType) {

        MessageVo messageVo = new MessageVo();
        messageVo.setShopId(shopID);
        messageVo.setContactId(CacheUtil.getInstance().getUserId());
        messageVo.setContactName(CacheUtil.getInstance().getUserName());
        messageVo.setSessionId(sessionId);
        messageVo.setMessageId(tempMessageId);
        messageVo.setTempId(tempMessageId);
        messageVo.setSendStatus(sendStatus);
        messageVo.setSendTime(sendTime);
        messageVo.setMimeType(MimeType.AUDIO);
        messageVo.setAttachId(attachId);
        messageVo.setFileName(fileName);
        messageVo.setFilePath(filePath);
        messageVo.setVoiceTime(voiceTime+"");
        messageVo.setRuleType(ruleType);
        messageVo.setIsRead(true);
        return messageVo;
    }

    /**
     * 响应获取历史聊天记录
     * @param messageList
     */
    public void responsePageMessages(List<MessageVo> messageList) {
        if (null != messageList && !messageList.isEmpty()) {
            int messageRecordSize = messageList.size();
            MessageVo messageVo = null;
            for (int i = 0; i < (messageRecordSize > 10 ? 10
                    : messageRecordSize); i++) {
                messageVo = messageList.get(i);
                String messageId = messageVo.getMessageId();
                if (!messageVector.contains(messageId) && i < 10) {
                    currentMessageList.add(0, messageVo);
                    messageVector.add(messageId);
                }
            }
        }
        chatAdapter.setData(currentMessageList);
        if (null != messageListView) {
            if (!currentMessageList.isEmpty()) {
                messageListView.setSelection(currentMessageList.size());
            }
            messageListView.stopRefresh();
        }
    }

    /**
     * 查询一页数据
     * @param shopID
     * @param limitSize
     * @param lastSendTime
     * @param isFirstTime
     */
    public void queryPageMessages(final String sessionID, final String shopID, final String clientID,
                                  final int limitSize, final long lastSendTime, final boolean isFirstTime) {

        long startTime, endTime, midTime = 0;
        int  localCount;
        requestMessageList = new ArrayList<>();
        if (isFirstTime) {
            requestMessageList = (ArrayList<MessageVo>)MessageDBUtil.getInstance().
                    queryMessageListBySessionID(sessionID, lastSendTime, limitSize, true);
        } else {
            requestMessageList = (ArrayList<MessageVo>)MessageDBUtil.getInstance().
                    queryMessageListBySessionID(sessionID, lastSendTime, limitSize, false);
        }
        Collections.reverse(requestMessageList);
        localCount = requestMessageList.size();
        if (localCount > 0) {
            if (NetWorkUtil.isNetworkConnected(context)) {
                startTime = requestMessageList.get(localCount - 1).getSendTime();
                endTime   = requestMessageList.get(0).getSendTime();
                if (localCount > 10) {
                    midTime = requestMessageList.get(9).getSendTime();
                }
                // step6 从服务器获取最新数据并更新
                //TODO Jimmy 同步后台数据，防止数据丢失
            }
            loadSqlteData(localCount, requestMessageList, shopID, isFirstTime);

        } else if (localCount == 0) {
            //根据SessionID获取聊天历史记录
            if (NetWorkUtil.isNetworkConnected(context)) {
                //获取聊天历史请求，获取一页数据
                HashMap<String,String> bizMap = new HashMap<>();
                bizMap.put("SessionID", sessionID);//会话ID
                bizMap.put("ShopID", shopID);//商家ID
                bizMap.put("UserID", clientID);//用户ID
                bizMap.put("FromTime", "" + System.currentTimeMillis());//起始时间
                bizMap.put("Count", "" + PRE_LOAD_PAGE_SIZE);//获取消息条数
                NetRequest mediaRequest = new NetRequest(ConfigUtil.getInst().getMediaDomain() + "msg/find/sessionid");
                mediaRequest.setBizParamMap(bizMap);
                NetRequestTask mediaRequestTask = new NetRequestTask(context, mediaRequest, NetResponse.class);
                mediaRequestTask.setNetRequestListener(new NetRequestListener() {
                    @Override
                    public void onNetworkRequestError(int errorCode, String errorMessage) {
                        Log.i(TAG, "errorCode:" + errorCode);
                        Log.i(TAG, "errorMessage:" + errorMessage);
                    }

                    @Override
                    public void onNetworkRequestCancelled() {

                    }

                    @Override
                    public void onNetworkResponseSucceed(NetResponse result) {
                        Log.i(TAG, "获取聊天历史记录数据 rawResult:" + result.rawResult);
                        LogUtil.getInstance().info(LogLevel.INFO, "获取聊天历史记录数据 rawResult:" + result.rawResult);
                        //TODO Jimmy 1、数据库入库
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<MsgCustomerServiceChat>>() {
                        }.getType();
                        List<MsgCustomerServiceChat> chatLists = gson.fromJson(result.rawResult, type);
                        if (null != chatLists && !chatLists.isEmpty()) {
                            List<MessageVo> messageVos = new ArrayList<>();
                            for (MsgCustomerServiceChat msgChat : chatLists) {
                                MessageVo messageVo = MessageFactory.getInstance().buildMessageVoByMsgChat(msgChat);
                                if (null != messageVo) {
                                    messageVos.add(messageVo);
                                    long addResult = MessageDBUtil.getInstance().addMessage(msgChat);
                                    if (addResult > 0) {
                                        requestMessageList.add(messageVo);
                                        LogUtil.getInstance().info(LogLevel.INFO, " 历史记录消息添加数据库成功 addResult:" + addResult);
                                    } else {
                                        LogUtil.getInstance().info(LogLevel.INFO, " 历史记录消息添加失败 addResult:" + addResult);
                                    }
                                }
                            }

                            int requestCount = requestMessageList.size();
                            loadSqlteData(requestCount, requestMessageList, shopID, isFirstTime);

                            //TODO Jimmy 2、UI展示
                            if (!requestMessageList.isEmpty()) {
                                chatAdapter.setData(currentMessageList);
                                scrollBottom();
                            }
                        }
                    }

                    @Override
                    public void beforeNetworkRequestStart() {
                    }
                });
                mediaRequestTask.execute();
            }
        }
    }

    Runnable stopRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != messageListView) {
                messageListView.stopRefresh();
            }
        }
    };

    /**
     * 加载本地数据库消息记录
     * @param localCount
     * @param requestMessageList
     * @param shopID
     * @param isFirstTime
     */
    public void loadSqlteData(int localCount, ArrayList<MessageVo> requestMessageList,
                              String shopID, boolean isFirstTime) {
        if (null == currentMessageList) {
            currentMessageList = new ArrayList<>();
        }
        if (requestMessageList.size() > 10) {
            removeRange(requestMessageList, 0, 10);
        }
        currentMessageList.addAll(0, requestMessageList);
        if (null == chatAdapter) {
            chatAdapter = new ChatAdapter(context,currentMessageList);
        } else {
            chatAdapter.setData(currentMessageList);
        }
        for (MessageVo messageVoo : requestMessageList) {
            String messageId = messageVoo.getMessageId();
            if (!messageVector.contains(messageId)) {
                messageVector.add(messageId);
            }
        }
        if (null != messageListView) {
            if (isFirstTime) {
                messageListView.setSelection(localCount);
            } else {
                int count = requestMessageList != null ? requestMessageList
                        .size() : 0;
                messageListView.setSelection(count > 0 ? count - 1 : 0);
            }
            messageListView.stopRefresh();
        }
    }

    /**
     * 移除预加载多余数据
     * @param requestMessageList
     * @param fromIndex
     * @param toIndex
     */
    public void removeRange(List<MessageVo> requestMessageList,
                            int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; i++) {
            requestMessageList.remove(fromIndex);
        }
    }

    /**
     * 设置滚动位置
     */
    public void scrollBottom() {
        if (currentMessageList != null && currentMessageList.size() > 0) {
            messageListView.setSelection(currentMessageList.size() - 1);
        }
    }

    @Override
    public void onRefresh() {
        if (null == currentMessageList) {
            currentMessageList = new ArrayList<>();
        }
        if (!currentMessageList.isEmpty()) {
            lastSendTime = currentMessageList.get(0).getSendTime();
            // 查询数据库获得第一页数据
            queryPageMessages(mSessionID, mShopID, mClientID, PRE_LOAD_PAGE_SIZE, lastSendTime, false);
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
    public void onResend(MessageVo messageVo) {
        //消息重发送处理
        //1.删除消息条目
        //2.更新消息条目并将消息状态置为发送中
        if(messageVector.contains(messageVo.getTempId())) {
            messageVector.remove(messageVo.getTempId());
        }
        String tempID = UUIDBuilder.getInstance().getRandomUUID();
        if(currentMessageList.contains(messageVo)){
            currentMessageList.remove(messageVo);
        }
        String messageID = messageVo.getMessageId();
        messageVo.setMessageId(tempID);
        messageVo.setTempId(tempID);
        messageVo.setSendStatus(SendStatus.SENDING);
        MessageDBUtil.getInstance().updateMessageVoByMessageID(messageVo, messageID);
        messageVector.add(tempID);//临时消息ID
        currentMessageList.add(messageVo);
        //界面刷新
        chatAdapter.setData(currentMessageList);
        scrollBottom();
        sendMessageVo(messageVo);
    }

    public MsgListView getMessageListView() {
        return messageListView;
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

    @Override
    public void receive(String message) {
        LogUtil.getInstance().info(LogLevel.INFO, message.toString());
        if(TextUtils.isEmpty(message))
            return ;

        try {
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");
            Gson gson = null;

            /** 文本消息的回复 */
            if(ProtocolMSG.MSG_CustomerServiceTextChat_RSP == type) {
                if(null == gson){
                    gson = new Gson();
                }
                MsgCustomerServiceTextChatRSP msgTextRSP = gson.fromJson(message, MsgCustomerServiceTextChatRSP.class);
                int result = msgTextRSP.getResult();
                // 0:发送成功  1:发送失败  2:客人在线客服离线   3:客人离线客服在线 4:所有客服不在线
                if(0 == result){
                    // 更新数据库
                    String realMsgID = msgTextRSP.getSrvmsgid() + "";//服务器返回msgID
                    long   sendTime  = msgTextRSP.getTimestamp();//服务器返回发送时间
                    String tempID    = msgTextRSP.getTempid();//临时消息ID
                    messageVector.remove(tempID);//删除消息ID集合
                    // 1、更新是否已读状态
                    MessageDBUtil.getInstance().updateMessageSendSuccess(realMsgID, tempID, sendTime);
                    //查找更新后的消息对象
                    MessageVo messageChatVo = MessageDBUtil.getInstance().queryMessageByMessageID(realMsgID);
                    // 2. 添加发送消息的集合
                    messageVector.add(realMsgID);
                    if (null != messageChatVo && !currentMessageList.isEmpty()) {
                        for (int i = 0; i < currentMessageList.size(); i++) {
                            if (null != currentMessageList.get(i)
                                    && null != currentMessageList.get(i).getMessageId()
                                    && currentMessageList.get(i).getMessageId().equals(tempID)) {
                                currentMessageList.set(i, messageChatVo);
                            }
                        }
                    }
                    // 3、刷新页面
                    chatAdapter.setData(currentMessageList);
                    scrollBottom();
                } else if(3 == result){
                    showOfflineDialog();
                }
            }

            /** 音频消息的回复 */
            if(ProtocolMSG.MSG_CustomerServiceMediaChat_RSP == type) {
                if(null == gson){
                    gson = new Gson();
                }
                MsgCustomerServiceMediaChatRSP msgMediaRSP = gson.fromJson(message,
                        MsgCustomerServiceMediaChatRSP.class);
                int result = msgMediaRSP.getResult();
                if(0 == result){
                    // 更新数据库
                    String realMsgID = msgMediaRSP.getSrvmsgid() + "";//服务器返回msgID
                    long   sendTime  = msgMediaRSP.getTimestamp();//服务器返回发送时间
                    String tempID = msgMediaRSP.getTempid();//临时消息ID

                    if(messageVector.contains(tempID)){
                        messageVector.remove(tempID);//删除消息ID集合
                    }

                    // 1、更新是否已读状态
                    MessageDBUtil.getInstance().updateMessageSendSuccess(realMsgID, tempID, sendTime);
                    //查找更新后的消息对象
                    MessageVo messageChatVo = MessageDBUtil.getInstance().queryMessageByMessageID(realMsgID);
                    // 2. 添加发送消息的集合
                    messageVector.add(realMsgID);
                    if (null != messageChatVo && !currentMessageList.isEmpty()) {
                        for (int i = 0; i < currentMessageList.size(); i++) {
                            if (null != currentMessageList.get(i)
                                    && null != currentMessageList.get(i).getMessageId()
                                    && currentMessageList.get(i).getMessageId().equals(tempID)) {
                                currentMessageList.set(i, messageChatVo);
                            }
                        }
                    }
                    chatAdapter.setData(currentMessageList);
                    scrollBottom();
                } else if(3 == result){
                    showOfflineDialog();
                }
            }

            /** 图片消息的回复 */
            if(ProtocolMSG.MSG_CustomerServiceImgChat_RSP == type) {
                if(null == gson){
                    gson = new Gson();
                }
                MsgCustomerServiceImgChatRSP msgImgRSP = gson.fromJson(message,
                        MsgCustomerServiceImgChatRSP.class);
                int result = msgImgRSP.getResult();
                if(0 == result){
                    String realMsgID = msgImgRSP.getSrvmsgid() + "";//服务器返回msgID
                    long   sendTime  = msgImgRSP.getTimestamp();//服务器返回发送时间
                    String tempID    = msgImgRSP.getTempid();//临时消息ID

                    if(messageVector.contains(tempID)){
                        messageVector.remove(tempID);//删除消息ID集合
                    }
                    // 1、更新是否已读状态
                    MessageDBUtil.getInstance().updateMessageSendSuccess(realMsgID, tempID, sendTime);
                    //查找更新后的消息对象
                    MessageVo messageChatVo = MessageDBUtil.getInstance().queryMessageByMessageID(realMsgID);
                    // 2. 添加发送消息的集合
                    messageVector.add(realMsgID);
                    if (null != messageChatVo && !currentMessageList.isEmpty()) {
                        for (int i = 0; i < currentMessageList.size(); i++) {
                            if (null != currentMessageList.get(i)
                                    && null != currentMessageList.get(i).getMessageId()
                                    && currentMessageList.get(i).getMessageId().equals(tempID)) {
                                currentMessageList.set(i, messageChatVo);
                            }
                        }
                    }

                    // 3、刷新页面
                    chatAdapter.setData(currentMessageList);
                    scrollBottom();
                } else if(3 == result){
                    showOfflineDialog();
                }
            }

            if(ProtocolMSG.MSG_CustomerServiceTextChat == type){//接收别人发送文本消息
                if(null == gson){
                    gson = new Gson();
                }
                MsgCustomerServiceTextChat msgText = gson.fromJson(message, MsgCustomerServiceTextChat.class);
                String sessionID = msgText.getSessionid();
                if(sessionID.equals(mSessionID)){
                    mMessageVo = MessageFactory.getInstance().buildMessageVoByMsgText(msgText);
                    mMessageVo.setIsRead(true);
                    MessageDBUtil.getInstance().updateMessage(MessageFactory.
                            getInstance().buildContentValues(mMessageVo));
                    currentMessageList.add(mMessageVo);
                    chatAdapter.setData(currentMessageList);
                    scrollBottom();
                }
            }

            if(ProtocolMSG.MSG_CustomerServiceImgChat == type){//接收别人发送的图片消息
                if(null == gson){
                    gson = new Gson();
                }
                MsgCustomerServiceImgChat msgImg = gson.fromJson(message,
                        MsgCustomerServiceImgChat.class);
                String sessionID = msgImg.getSessionid();
                if(sessionID.equals(mSessionID)){
                    mMessageVo = MessageFactory.getInstance().buildMessageVoByMsgImg(msgImg);
                    mMessageVo.setIsRead(true);
                    MessageDBUtil.getInstance().updateMessage(MessageFactory.
                            getInstance().buildContentValues(mMessageVo));
                    currentMessageList.add(mMessageVo);
                    chatAdapter.setData(currentMessageList);
                    scrollBottom();
                }

            }

            if (ProtocolMSG.MSG_CustomerServiceMediaChat ==  type){//接收别人发送的语音消息
                if(null == gson){
                    gson = new Gson();
                }
                MsgCustomerServiceMediaChat msgMedia = gson.fromJson(message,
                        MsgCustomerServiceMediaChat.class);
                String sessionID = msgMedia.getSessionid();
                if(sessionID.equals(mSessionID)){
                    mMessageVo = MessageFactory.getInstance().buildMessageVoByMsgMedia(msgMedia);
                    mMessageVo.setIsRead(true);
                    MessageDBUtil.getInstance().updateMessage(MessageFactory.
                            getInstance().buildContentValues(mMessageVo));
                    currentMessageList.add(mMessageVo);
                    chatAdapter.setData(currentMessageList);
                    scrollBottom();
                }
            }

            if(ProtocolMSG.MSG_ShopDisbandSession_RSP == type){//解散回话
                if(null == gson){
                    gson = new Gson();
                }
                MsgShopDisbandSessionRSP msgShopDisbandSessionRSP = gson.fromJson(message,MsgShopDisbandSessionRSP.class);
                if(null != msgShopDisbandSessionRSP){
                    int result = msgShopDisbandSessionRSP.getResult();
                    if(0 == result){
                        //解散回话成功
                        showDisbandSessionDialog();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDisbandSessionDialog(){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示");
        customBuilder.setMessage("解散回话成功!");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(context instanceof Activity){
                    ((Activity)context).finish();
                }
            }
        });
        customBuilder.create().show();
    }

    private void showOfflineDialog(){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("温馨提示");
        customBuilder.setMessage("客人不在线，请稍后再发送!");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customBuilder.create().show();
    }
}