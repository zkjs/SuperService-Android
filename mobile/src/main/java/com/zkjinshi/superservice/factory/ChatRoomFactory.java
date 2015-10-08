package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.zkjinshi.superservice.entity.MsgCustomerServiceImgChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceMediaChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceTextChat;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.ChatRoomVo;
import com.zkjinshi.superservice.vo.ChatType;
import com.zkjinshi.superservice.vo.MessageVo;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatRoomFactory {

    private static ChatRoomFactory chatRoomFactory;

    private ChatRoomFactory() {
    }

    public synchronized static ChatRoomFactory getInstance(){
        if(null == chatRoomFactory){
            chatRoomFactory = new ChatRoomFactory();
        }
        return chatRoomFactory;
    }

    /**
     * 构建聊天室键值对
     * @return
     */
    public ContentValues buildContentValues(MsgCustomerServiceTextChat msgText){
        String sessionId = msgText.getSessionid();
        String shopId = msgText.getShopid();
        ChatType chatType = ChatType.PRIVATE;//默认私聊
        long createTime = System.currentTimeMillis();
        String contactId = msgText.getClientid();
        String title = msgText.getClientname();
        long lastAction = System.currentTimeMillis();
        boolean enabled = true;
        int noticeCount = 0;
        ContentValues values = new ContentValues();
        if(!TextUtils.isEmpty(sessionId)){
            values.put("chat_id", sessionId);
        }
        if(!TextUtils.isEmpty(shopId)){
            values.put("shop_id", shopId);
        }
        values.put("chat_type", chatType.getVlaue());
        values.put("create_time", createTime);
        if(!TextUtils.isEmpty(contactId)){
            values.put("creater_id", contactId);
        }
        if(!TextUtils.isEmpty(title)){
            values.put("title", title);
        }
        values.put("last_action",  lastAction);
        values.put("enabled", enabled == true ?  1: 0);
        values.put("notice_count", noticeCount);
        return values;
    }

    /**
     * 构建聊天室记录键值对
     * @return
     */
    public ContentValues buildContentValues(MsgCustomerServiceImgChat msgImg){
        String sessionId = msgImg.getSessionid();
        String shopId = msgImg.getShopid();
        ChatType chatType = ChatType.PRIVATE;//默认私聊
        long createTime = System.currentTimeMillis();
        String contactId = msgImg.getClientid();
        String title = msgImg.getClientname();
        long lastAction = System.currentTimeMillis();
        boolean enabled = true;
        int noticeCount = 0;
        ContentValues values = new ContentValues();
        if(!TextUtils.isEmpty(sessionId)){
            values.put("chat_id", sessionId);
        }
        if(!TextUtils.isEmpty(shopId)){
            values.put("shop_id", shopId);
        }
        values.put("chat_type", chatType.getVlaue());
        values.put("create_time", createTime);
        if(!TextUtils.isEmpty(contactId)){
            values.put("creater_id", contactId);
        }
        if(!TextUtils.isEmpty(title)){
            values.put("title", title);
        }
        values.put("last_action",  lastAction);
        values.put("enabled", enabled == true ?  1: 0);
        values.put("notice_count", noticeCount);
        return values;
    }

    /**
     * 构建聊天室记录键值对
     * @return
     */
    public ContentValues buildContentValues(MsgCustomerServiceMediaChat msgMedia){
        String sessionId = msgMedia.getSessionid();
        String shopId = msgMedia.getShopid();
        ChatType chatType = ChatType.PRIVATE;//默认私聊
        long createTime = System.currentTimeMillis();
        String contactId = msgMedia.getClientid();
        String title = msgMedia.getClientname();
        long lastAction = System.currentTimeMillis();
        boolean enabled = true;
        int noticeCount = 0;
        ContentValues values = new ContentValues();
        if(!TextUtils.isEmpty(sessionId)){
            values.put("chat_id", sessionId);
        }
        if(!TextUtils.isEmpty(shopId)){
            values.put("shop_id", shopId);
        }
        values.put("chat_type", chatType.getVlaue());
        values.put("create_time", createTime);
        if(!TextUtils.isEmpty(contactId)){
            values.put("creater_id", contactId);
        }
        if(!TextUtils.isEmpty(title)){
            values.put("title", title);
        }
        values.put("last_action",  lastAction);
        values.put("enabled", enabled == true ?  1: 0);
        values.put("notice_count", noticeCount);
        return values;
    }

    /**
     * 根据游标创建聊天室对象
     * @param cursor
     * @return
     */
    public ChatRoomVo buildChatRoomVo(Cursor cursor) {
        String chatId = cursor.getString(0);
        String shopId = cursor.getString(1);
        ChatType chatType = ChatType.PRIVATE;
        long createTime = cursor.getLong(3);
        String createrId = cursor.getString(4);
        String imageUrl = cursor.getString(5);
        String title = cursor.getString(6);
        long lastAction = cursor.getLong(7);
        boolean enabled = cursor.getInt(8) == 1 ? true : false;
        int noticeCount = cursor.getInt(9);
        ChatRoomVo chatRoom = new ChatRoomVo();
        if(!TextUtils.isEmpty(chatId)){
            chatRoom.setChatId(chatId);
        }
        if(!TextUtils.isEmpty(shopId)){
            chatRoom.setShopId(shopId);
        }
        chatRoom.setChatType(chatType);
        chatRoom.setCreateTime(createTime);
        if(!TextUtils.isEmpty(createrId)){
            chatRoom.setCreaterId(createrId);
        }
        if(!TextUtils.isEmpty(imageUrl)){
            chatRoom.setImageUrl(imageUrl);
        }
        if(!TextUtils.isEmpty(title)){
            chatRoom.setTitle(title);
        }
        chatRoom.setLastAction(lastAction);
        chatRoom.setEnabled(enabled);
        chatRoom.setNoticeCount(noticeCount);
        return chatRoom;
    }

}
