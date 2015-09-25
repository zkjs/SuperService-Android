package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.superservice.entity.MsgCustomerServiceChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceImgChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceMediaChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceTextChat;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.MessageVo;
import com.zkjinshi.superservice.vo.MimeType;
import com.zkjinshi.superservice.vo.SendStatus;

/**
 * 消息记录工厂类
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageFactory {

    private final static String TAG = MessageFactory.class.getSimpleName();

    private MessageFactory() {
    }

    private static MessageFactory instance;

    public synchronized static MessageFactory getInstance() {
        if (null == instance) {
            instance = new MessageFactory();
        }
        return instance;
    }

    /**
     * 构建消息表键值对
     * @return
     */
    public ContentValues buildContentValues(MessageVo messageVo){
        ContentValues values = new ContentValues();
        values.put("message_id", messageVo.getMessageId());
        values.put("temp_id", messageVo.getTempId());
        values.put("session_id", messageVo.getSessionId());
        values.put("shop_id", messageVo.getShopId());
        values.put("contact_id", messageVo.getContactId());
        values.put("contact_name", messageVo.getContactName());//发送者名称
        values.put("content", messageVo.getContent());//消息内容
        values.put("send_time", messageVo.getSendTime());//发送时间
        values.put("title", messageVo.getTitle());//聊天室名称
        values.put("voice_time", messageVo.getVoiceTime());//语音时间
        values.put("mime_type", messageVo.getMimeType().getVlaue());//消息类型
        values.put("send_status", messageVo.getSendStatus().getVlaue());//发送状态
        values.put("is_read", messageVo.isRead());//是否已读 0未读 1已读
        values.put("attach_id", messageVo.getAttachId());//附件id
        values.put("rule_type", messageVo.getRuleType());
        values.put("file_name", messageVo.getFileName());
        values.put("file_path", messageVo.getFilePath());
        values.put("url", messageVo.getUrl());
        values.put("scale_url", messageVo.getScaleUrl());
        return values;
    }

    /**
     * 构建消息表键值对
     * @return
     */
    public ContentValues buildContentValues(MsgCustomerServiceTextChat msgText){
        ContentValues values = new ContentValues();
        long srvID    = msgText.getSrvmsgid();
        String tempID = msgText.getTempid();

        if(srvID > 0) {
            values.put("message_id", ""+msgText.getSrvmsgid());
        } else {
            if(!TextUtils.isEmpty(tempID)){
                values.put("message_id", tempID);
            }
        }
        if(!TextUtils.isEmpty(tempID)){
            values.put("temp_id", tempID);//临时消息id
        }

        String sessionID = msgText.getSessionid();
        if(!TextUtils.isEmpty(sessionID)){
            values.put("session_id", sessionID);
        }

        String shopID = msgText.getSessionid();
        if(!TextUtils.isEmpty(shopID)){
            values.put("shop_id", msgText.getShopid());
        }

        String contactID = msgText.getFromid();
        if(!TextUtils.isEmpty(contactID)){
            values.put("contact_id", contactID);
        }

        String contactName = msgText.getFromname();
        if(!TextUtils.isEmpty(contactName)){
            values.put("contact_name", contactName);//发送者名称
        }

        String textContent = msgText.getTextmsg();
        if(!TextUtils.isEmpty(textContent)){
            values.put("content", textContent);//消息内容
        }
        values.put("send_time", msgText.getTimestamp());//发送时间
        String title = msgText.getShopid();
        if(!TextUtils.isEmpty(title)){
            values.put("title", title);//聊天室名称
        }

        values.put("voice_time", 0);//语音时间
        values.put("mime_type", msgText.getChildtype() == 0 ? MimeType.TEXT.getVlaue() : MimeType.CARD.getVlaue());//消息类型
        values.put("send_status", 1);//发送状态
        values.put("is_read", 0);//是否已读 0未读 1已读
        values.put("attach_id", "");//附件id

        String ruleType = msgText.getRuletype();
        if(!TextUtils.isEmpty(ruleType)){
            values.put("rule_type", ruleType);
        }

        return values;
    }

    /**
     * 构建消息表键值对
     * @return
     */
    public ContentValues buildContentValues(MsgCustomerServiceMediaChat msgMedia){
        ContentValues values = new ContentValues();
        long srvID    = msgMedia.getSrvmsgid();
        String tempID = msgMedia.getTempid();

        if(srvID > 0) {
            values.put("message_id", "" + srvID);
        } else {
            if(!TextUtils.isEmpty(tempID)){
                values.put("message_id", tempID);
            }
        }
        if(!TextUtils.isEmpty(tempID)){
            values.put("temp_id", tempID);//临时消息id
        }

        String sessionID = msgMedia.getSessionid();
        if(!TextUtils.isEmpty(sessionID)){
            values.put("session_id", sessionID);
        }

        String shopID = msgMedia.getSessionid();
        if(!TextUtils.isEmpty(shopID)){
            values.put("shop_id", msgMedia.getShopid());
        }

        String contactID = msgMedia.getFromid();
        if(!TextUtils.isEmpty(contactID)){
            values.put("contact_id", contactID);
        }

        String contactName = msgMedia.getFromname();
        if(!TextUtils.isEmpty(contactName)){
            values.put("contact_name", contactName);//发送者名称
        }

        values.put("send_time", msgMedia.getTimestamp());//发送时间
        String title = msgMedia.getShopid();
        if(!TextUtils.isEmpty(title)){
            values.put("title", title);//聊天室名称
        }
        values.put("voice_time", msgMedia.getDurnum());//语音时间
        values.put("mime_type", MimeType.AUDIO.getVlaue());//消息类型
        values.put("send_status", 1);//发送状态
        values.put("is_read", 0);//是否已读

        String attachID = msgMedia.getBody();
        if(!TextUtils.isEmpty(attachID)){
            values.put("attach_id", attachID);//附件id 图片本地保存路径
        }
        String ruleType = msgMedia.getRuletype();
        if(!TextUtils.isEmpty(ruleType)){
            values.put("rule_type", ruleType);
        }

        String url = msgMedia.getUrl();
        if(!TextUtils.isEmpty(url)){
            values.put("url", url);
        }
        String fileName = msgMedia.getFilename();
        String filePath = msgMedia.getFilePath();
        if(!TextUtils.isEmpty(fileName)){
            values.put("file_name",fileName);
        }
        if(!TextUtils.isEmpty(filePath)){
            values.put("file_path",filePath);
        }
        return values;
    }

    /**
     * 构建消息表键值对
     * @return
     */
    public ContentValues buildContentValues(MsgCustomerServiceImgChat msgImg){
        ContentValues values = new ContentValues();
        long   srvID  = msgImg.getSrvmsgid();
        String tempID = msgImg.getTempid();

        if(srvID > 0) {
            values.put("message_id", "" + srvID);
        } else {
            if(!TextUtils.isEmpty(tempID)){
                values.put("message_id", tempID);
            }
        }
        if(!TextUtils.isEmpty(tempID)){
            values.put("temp_id", tempID);//临时消息id
        }

        String sessionID = msgImg.getSessionid();
        if(!TextUtils.isEmpty(sessionID)){
            values.put("session_id", sessionID);
        }

        String shopID = msgImg.getSessionid();
        if(!TextUtils.isEmpty(shopID)){
            values.put("shop_id", shopID);
        }

        String contactID = msgImg.getFromid();
        if(!TextUtils.isEmpty(contactID)){
            values.put("contact_id", contactID);
        }

        String contactName = msgImg.getFromname();
        if(!TextUtils.isEmpty(contactName)){
            values.put("contact_name", contactName);//发送者名称
        }

        values.put("send_time", msgImg.getTimestamp());//发送时间
        String title = msgImg.getShopid();
        if(!TextUtils.isEmpty(title)){
            values.put("title", title);//聊天室名称
        }
        values.put("voice_time", 0);//无语音时间
        values.put("mime_type", MimeType.IMAGE.getVlaue());//消息类型
        values.put("send_status", 1);//发送状态
        values.put("is_read", 0);//是否已读
        String attachID = msgImg.getBody();
        if(!TextUtils.isEmpty(attachID)){
            values.put("attach_id", msgImg.getBody());//附件id 图片本地保存路径
        }
        values.put("temp_id", tempID);//临时消息id
        String ruleType = msgImg.getRuletype();
        if(!TextUtils.isEmpty(ruleType)){
            values.put("rule_type", ruleType);
        }

        String url = msgImg.getUrl();
        if(!TextUtils.isEmpty(url)){
            values.put("url", url);
        }

        String scaleUrl = msgImg.getScaleurl();
        if(!TextUtils.isEmpty(scaleUrl)){
            values.put("scale_url", scaleUrl);
        }
        String fileName = msgImg.getFilename();
        String filePath = msgImg.getFilePath();
        if(!TextUtils.isEmpty(fileName)){
            values.put("file_name",fileName);
        }
        if(!TextUtils.isEmpty(filePath)){
            values.put("file_path",filePath);
        }
        return values;
    }

    /**
     * 构建聊天消息键对值
     *
     * @param msgChat
     * @return
     */
    public ContentValues buildContentValues(MsgCustomerServiceChat msgChat) {
        ContentValues values = new ContentValues();
        int type = msgChat.getType();
        if (ProtocolMSG.MSG_CustomerServiceTextChat == type) {
            MsgCustomerServiceTextChat msgTextChat = buildMsgTextByMsgChat(msgChat);
            values = buildContentValues(msgTextChat);
        }

        if (ProtocolMSG.MSG_CustomerServiceMediaChat == type) {
            MsgCustomerServiceMediaChat msgMedia = buildMsgMediaByMsgChat(msgChat);
            values = buildContentValues(msgMedia);
        }

        if (ProtocolMSG.MSG_CustomerServiceImgChat == type) {
            MsgCustomerServiceImgChat msgImgChat = buildMsgImgByMsgChat(msgChat);
            values = buildContentValues(msgImgChat);
        }
        values.put("is_read", 1);//是否已读 0未读 1已读
        return values;
    }

    /**
     * 根据cursor构建MessageVo
     * @param cursor
     * @return
     */
    public MessageVo buildMessageVo(Cursor cursor){
        MessageVo messageVo = new MessageVo();
        messageVo.setMessageId(cursor.getString(0));
        messageVo.setSessionId(cursor.getString(1));
        messageVo.setShopId(cursor.getString(2));
        messageVo.setContactId(cursor.getString(3));
        messageVo.setContactName(cursor.getString(4));
        messageVo.setContent(cursor.getString(5));
        messageVo.setSendTime(cursor.getLong(6));
        messageVo.setTitle(cursor.getString(7));
        messageVo.setVoiceTime(cursor.getString(8));
        messageVo.setMimeType(getMimeType(cursor.getInt(9)));
        messageVo.setSendStatus(getSendStatus(cursor.getInt(10)));
        messageVo.setIsRead(cursor.getInt(11) == 1 ? true : false);
        messageVo.setAttachId(cursor.getString(12));
        messageVo.setTempId(cursor.getString(13));
        messageVo.setRuleType(cursor.getString(14));
        messageVo.setFileName(cursor.getString(15));
        messageVo.setFilePath(cursor.getString(16));
        messageVo.setUrl(cursor.getString(17));
        messageVo.setScaleUrl(cursor.getString(18));
        return  messageVo;
    }

    /**
     * 根据文本消息条目构建MessageVo
     * @param msgText
     * @return
     */
    public MessageVo buildMessageVoByMsgText(MsgCustomerServiceTextChat msgText){
        MessageVo messageVo = new MessageVo();
        messageVo.setMessageId(msgText.getSrvmsgid() + "");
        messageVo.setShopId(msgText.getShopid());
        messageVo.setSessionId(msgText.getSessionid());
        messageVo.setContactId(msgText.getFromid());
        messageVo.setContactName(msgText.getFromname());
        messageVo.setContent(msgText.getTextmsg());
        messageVo.setSendTime(msgText.getTimestamp());
        messageVo.setTitle(msgText.getFromname());
        messageVo.setMimeType(msgText.getChildtype() == 0 ? MimeType.TEXT : MimeType.CARD);
        messageVo.setSendStatus(SendStatus.SEND_SUCCESS);
        messageVo.setIsRead(false);
        messageVo.setAttachId("");
        messageVo.setTempId(msgText.getTempid() + "");
        messageVo.setRuleType(msgText.getRuletype());
        return  messageVo;
    }

    /**
     * 根据MessageVo构建文本消息条目
     * @param messageVo
     * @return
     */
    public MsgCustomerServiceTextChat buildMsgTextByMessageVo(MessageVo messageVo){
        MsgCustomerServiceTextChat msgText = new MsgCustomerServiceTextChat();
        msgText.setSrvmsgid(0);
        msgText.setType(ProtocolMSG.MSG_CustomerServiceTextChat);
        msgText.setTimestamp(System.currentTimeMillis());
        msgText.setFromid(CacheUtil.getInstance().getUserId());
        msgText.setFromname(CacheUtil.getInstance().getUserName());
        msgText.setShopid(messageVo.getShopId());
        msgText.setClientid(CacheUtil.getInstance().getUserId());
        msgText.setClientname(CacheUtil.getInstance().getUserName());
        msgText.setTextmsg(messageVo.getContent());
        msgText.setSessionid(messageVo.getSessionId());
        msgText.setTempid(messageVo.getTempId());
        msgText.setRuletype(messageVo.getRuleType());
        msgText.setChildtype(messageVo.getMimeType() == MimeType.TEXT ? 0 : 1);
        return msgText;
    }

    /**
     * 根据语音消息条目构建MessageVo
     * @param msgMedia
     * @return
     */
    public MessageVo buildMessageVoByMsgMedia(MsgCustomerServiceMediaChat msgMedia) {
        MessageVo messageVo = new MessageVo();
        messageVo.setMessageId(msgMedia.getSrvmsgid() + "");
        messageVo.setSessionId(msgMedia.getSessionid());
        messageVo.setContactId(msgMedia.getFromid());
        messageVo.setContactName(msgMedia.getFromname());
        messageVo.setSendTime(msgMedia.getTimestamp());
        messageVo.setTitle(msgMedia.getFromname());
        messageVo.setVoiceTime(msgMedia.getDurnum());
        messageVo.setMimeType(MimeType.AUDIO);
        messageVo.setSendStatus(SendStatus.SEND_SUCCESS);
        messageVo.setIsRead(false);
        messageVo.setAttachId(msgMedia.getBody());//录音保存路径
        messageVo.setTempId(msgMedia.getTempid());
        messageVo.setRuleType(msgMedia.getRuletype());
        messageVo.setUrl(msgMedia.getUrl());
        messageVo.setFileName(msgMedia.getFilename());
        return messageVo;
    }

    public MessageVo buildMessageVoByMsgMedia(MsgCustomerServiceMediaChat msgMedia, String audioPath){
        MessageVo messageVo = new MessageVo();
        messageVo.setMessageId(msgMedia.getSrvmsgid() + "");
        messageVo.setSessionId(msgMedia.getSessionid());
        messageVo.setContactId(msgMedia.getFromid());
        messageVo.setContactName(msgMedia.getFromname());
        messageVo.setSendTime(msgMedia.getTimestamp());
        messageVo.setTitle(msgMedia.getFromname());
        messageVo.setVoiceTime(msgMedia.getDurnum());
        messageVo.setMimeType(MimeType.AUDIO);
        messageVo.setSendStatus(SendStatus.SEND_SUCCESS);
        messageVo.setIsRead(false);
        messageVo.setAttachId(msgMedia.getBody());//录音保存路径
        messageVo.setTempId(msgMedia.getTempid());
        messageVo.setRuleType(msgMedia.getRuletype());
        messageVo.setUrl(msgMedia.getUrl());
        messageVo.setFileName(msgMedia.getFilename());
        messageVo.setFilePath(audioPath);
        return  messageVo;
    }

    /**
     * 根据MessageVo构建音频消息条目
     * @param messageVo
     * @return
     */
    public MsgCustomerServiceMediaChat buildMsgMediaByMessageVo(MessageVo messageVo){
        MsgCustomerServiceMediaChat msgMedia = new MsgCustomerServiceMediaChat();
        msgMedia.setSrvmsgid(0);
        msgMedia.setFilePath(messageVo.getFilePath());
        msgMedia.setType(ProtocolMSG.MSG_CustomerServiceMediaChat);
        msgMedia.setTimestamp(System.currentTimeMillis());
        msgMedia.setShopid(messageVo.getShopId());
        msgMedia.setFromid(CacheUtil.getInstance().getUserId());
        msgMedia.setFromname(CacheUtil.getInstance().getUserName());
        msgMedia.setClientid(CacheUtil.getInstance().getUserId());
        msgMedia.setClientname(CacheUtil.getInstance().getUserName());
        msgMedia.setSessionid(messageVo.getSessionId());
        msgMedia.setDurnum(messageVo.getVoiceTime());
        msgMedia.setFilename(messageVo.getFileName());
        msgMedia.setBody(messageVo.getAttachId());
        msgMedia.setTempid(messageVo.getTempId());
        msgMedia.setRuletype(messageVo.getRuleType());
        msgMedia.setUrl(messageVo.getUrl());
        return msgMedia;
    }

    /**
     * 根据语音消息条目构建MessageVo
     * @param msgImg
     * @return
     */
    public MessageVo buildMessageVoByMsgImg(MsgCustomerServiceImgChat msgImg){
        MessageVo messageVo = new MessageVo();
        messageVo.setMessageId(msgImg.getSrvmsgid() + "");
        messageVo.setSessionId(msgImg.getSessionid());
        messageVo.setContactId(msgImg.getFromid());
        messageVo.setContactName(msgImg.getFromname());
        messageVo.setSendTime(msgImg.getTimestamp());
        messageVo.setTitle(msgImg.getFromname());
        messageVo.setMimeType(MimeType.IMAGE);
        messageVo.setSendStatus(SendStatus.SEND_SUCCESS);
        messageVo.setIsRead(false);
        messageVo.setAttachId(msgImg.getBody());
        messageVo.setTempId(msgImg.getTempid() + "");
        messageVo.setRuleType(msgImg.getRuletype());
        messageVo.setUrl(msgImg.getUrl());
        messageVo.setScaleUrl(msgImg.getScaleurl());
        messageVo.setFileName(msgImg.getFilename());
        return  messageVo;
    }

    /**
     * 根据MessageVo构建图片消息条目
     * @param messageVo
     * @return
     */
    public MsgCustomerServiceImgChat buildMsgImgByMessageVo(MessageVo messageVo){
        MsgCustomerServiceImgChat msgImg = new MsgCustomerServiceImgChat();
        msgImg.setSrvmsgid(0);
        msgImg.setType(ProtocolMSG.MSG_CustomerServiceImgChat);
        msgImg.setAttachId(messageVo.getAttachId());
        msgImg.setFilePath(messageVo.getFilePath());
        msgImg.setTimestamp(System.currentTimeMillis());
        msgImg.setShopid(messageVo.getShopId());
        msgImg.setFromid(CacheUtil.getInstance().getUserId());
        msgImg.setFromname(CacheUtil.getInstance().getUserName());
        msgImg.setClientid(CacheUtil.getInstance().getUserId());
        msgImg.setClientname(CacheUtil.getInstance().getUserName());
        msgImg.setSessionid(messageVo.getSessionId());
        msgImg.setFilename(messageVo.getFileName());
        msgImg.setBody(messageVo.getAttachId());
        msgImg.setTempid(messageVo.getTempId());
        msgImg.setRuletype(messageVo.getRuleType());
        msgImg.setUrl(messageVo.getUrl());
        msgImg.setScaleurl(messageVo.getScaleUrl());
        String imgName = messageVo.getFileName();
        String imgFormat = null;
        if(!TextUtils.isEmpty(imgName) && imgName.contains(".")){
            imgFormat = imgName.substring(imgName.lastIndexOf(".") + 1);
        }
        msgImg.setFormat(imgFormat);
        return msgImg;
    }


    /**
     * 根据语音消息条目构建MessageVo
     *
     * @param msgChat
     * @return
     */
    public MessageVo buildMessageVoByMsgChat(MsgCustomerServiceChat msgChat) {
        MessageVo messageVo = null;
        if (msgChat.getType() == ProtocolMSG.MSG_CustomerServiceTextChat) {
            MsgCustomerServiceTextChat msgText = buildMsgTextByMsgChat(msgChat);
            messageVo = buildMessageVoByMsgText(msgText);
        }

        if (msgChat.getType() == ProtocolMSG.MSG_CustomerServiceMediaChat) {
            MsgCustomerServiceMediaChat msgMedia = buildMsgMediaByMsgChat(msgChat);
            messageVo = buildMessageVoByMsgMedia(msgMedia);
        }

        if (msgChat.getType() == ProtocolMSG.MSG_CustomerServiceImgChat) {
            MsgCustomerServiceImgChat msgImg = buildMsgImgByMsgChat(msgChat);
            messageVo = buildMessageVoByMsgImg(msgImg);
        }
        messageVo.setIsRead(true);
        return messageVo;
    }

    /**
     * 根据MessageVo构建文本消息条目
     *
     * @param msgChat
     * @return
     */
    public MsgCustomerServiceTextChat buildMsgTextByMsgChat(MsgCustomerServiceChat msgChat) {
        MsgCustomerServiceTextChat msgText = new MsgCustomerServiceTextChat();

        msgText.setType(ProtocolMSG.MSG_CustomerServiceTextChat);
        msgText.setTimestamp(msgChat.getTimestamp());
        msgText.setTempid(msgChat.getTempid());
        msgText.setSrvmsgid(msgChat.getSrvmsgid());
        msgText.setProtover(msgChat.getProtover());

        msgText.setFromid(msgChat.getFromid());
        msgText.setFromname(msgChat.getFromname());
        msgText.setClientid(msgChat.getClientid());
        msgText.setClientname(msgChat.getClientname());
        msgText.setShopid(msgChat.getShopid());
        msgText.setRuletype(msgChat.getRuletype());
        msgText.setAdminid(msgChat.getAdminid());
        msgText.setSessionid(msgChat.getSessionid());
        msgText.setSeqid(msgChat.getSeqid());
        msgText.setIsreadack(msgChat.getIsreadack());
        msgText.setTextmsg(msgChat.getTextmsg());
        msgText.setChildtype(msgChat.getChildtype());
        return msgText;
    }

    /**
     * 根据MessageVo构建音频消息条目
     *
     * @param msgChat
     * @return
     */
    public MsgCustomerServiceMediaChat buildMsgMediaByMsgChat(MsgCustomerServiceChat msgChat) {

        MsgCustomerServiceMediaChat msgMedia = new MsgCustomerServiceMediaChat();
        msgMedia.setType(ProtocolMSG.MSG_CustomerServiceMediaChat);
        msgMedia.setTimestamp(msgChat.getTimestamp());
        msgMedia.setTempid(msgChat.getTempid());
        msgMedia.setSrvmsgid(msgChat.getSrvmsgid());
        msgMedia.setProtover(msgChat.getProtover());

        msgMedia.setFilePath(msgChat.getFilePath());
        msgMedia.setTimestamp(System.currentTimeMillis());
        msgMedia.setShopid(msgChat.getShopid());
        msgMedia.setFromid(msgChat.getFromid());
        msgMedia.setFromname(msgChat.getFromname());
        msgMedia.setClientid(msgChat.getClientid());
        msgMedia.setClientname(msgChat.getClientname());
        msgMedia.setSessionid(msgChat.getSessionid());
        msgMedia.setDurnum(msgChat.getDurnum());
        msgMedia.setFilename(msgChat.getFilename());
        msgMedia.setBody(msgChat.getAttachId());
        msgMedia.setTempid(msgChat.getTempid());
        msgMedia.setRuletype(msgChat.getRuletype());
        msgMedia.setUrl(msgChat.getUrl());
        return msgMedia;
    }

    /**
     * 根据MessageVo构建图片消息条目
     *
     * @param msgChat
     * @return
     */
    public MsgCustomerServiceImgChat buildMsgImgByMsgChat(MsgCustomerServiceChat msgChat) {

        MsgCustomerServiceImgChat msgImg = new MsgCustomerServiceImgChat();
        msgImg.setType(ProtocolMSG.MSG_CustomerServiceImgChat);
        msgImg.setTimestamp(msgChat.getTimestamp());
        msgImg.setTempid(msgChat.getTempid());
        msgImg.setSrvmsgid(msgChat.getSrvmsgid());
        msgImg.setProtover(msgChat.getProtover());

        msgImg.setAttachId(msgChat.getAttachId());
        msgImg.setFilePath(msgChat.getFilePath());
        msgImg.setShopid(msgChat.getShopid());
        msgImg.setFromid(msgChat.getFromid());
        msgImg.setFromname(msgChat.getFromname());
        msgImg.setClientid(msgChat.getClientid());
        msgImg.setClientname(msgChat.getClientname());
        msgImg.setSessionid(msgChat.getSessionid());
        msgImg.setFilename(msgChat.getFilename());
        msgImg.setBody(msgChat.getBody());
        msgImg.setTempid(msgChat.getTempid());
        msgImg.setRuletype(msgChat.getRuletype());
        msgImg.setUrl(msgChat.getUrl());
        msgImg.setScaleurl(msgChat.getScaleurl());
        msgImg.setFormat(msgChat.getFormat());
        return msgImg;
    }

    /**
     * 获取文件类型
     * @param mimeTypeValue
     * @return
     */
    public MimeType getMimeType(int mimeTypeValue){
        if(1 ==  mimeTypeValue){
            return MimeType.AUDIO;
        }else if(2 == mimeTypeValue){
            return MimeType.IMAGE;
        }else if(3 == mimeTypeValue){
            return MimeType.VIDEO;
        }else if(4 == mimeTypeValue){
            return MimeType.APPLICATION;
        } else if (5 == mimeTypeValue) {
            return MimeType.CARD;
        }
        return  MimeType.TEXT;
    }

    /**
     * 获取发送状态
     * @param sendStatusValue
     * @return
     */
    public SendStatus getSendStatus(int sendStatusValue){
        if( 0 == sendStatusValue ){
            return SendStatus.SEND_FAIL;
        }else if( 1 == sendStatusValue ){
            return SendStatus.SEND_SUCCESS;
        }
        return  SendStatus.SENDING;
    }

}
