package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.factory.ChatRoomFactory;
import com.zkjinshi.superservice.vo.ChatRoomVo;
import com.zkjinshi.superservice.vo.MessageVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatRoomDBUtil {

    private final static String TAG = ChatRoomDBUtil.class.getSimpleName();

    private Context      context;
    private DBOpenHelper helper;
    private static ChatRoomDBUtil instance;

    private ChatRoomDBUtil(){
    }

    public synchronized static ChatRoomDBUtil getInstance(){
        if(null == instance){
            instance = new ChatRoomDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = ServiceApplication.getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 插入单条图片聊天室记录
     * @return
     */
    public long addChatRoom(MessageVo messageVo){
        ContentValues values = ChatRoomFactory.getInstance().buildAddContentValues(messageVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            try {
                id = db.insert(DBOpenHelper.CHAT_ROOM_TBL, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(id == -1){
                id = db.update(DBOpenHelper.CHAT_ROOM_TBL, values, " session_id = ? ", new String[]{messageVo.getSessionId()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 插入单条图片聊天室记录
     * @return
     */
    public long addChatRoom(ChatRoomVo chatRoom){
        ContentValues values = ChatRoomFactory.getInstance().buildAddContentValues(chatRoom);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            try {
                id = db.insert(DBOpenHelper.CHAT_ROOM_TBL, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(id == -1){
                id = db.update(DBOpenHelper.CHAT_ROOM_TBL, values, " session_id = ? ", new String[]{chatRoom.getSessionid()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 更新图片单条聊天室记录
     * @return
     */
    public long updateChatRoom(MessageVo messageVo){
        ContentValues values = ChatRoomFactory.getInstance().buildUpdateContentValues(messageVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.update(DBOpenHelper.CHAT_ROOM_TBL, values, " session_id = ? ",
                    new String[]{messageVo.getSessionId()});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".updateChatRoom->" + e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 查询所有显示在消息中心聊天室记录表
     * @return
     */
    public List<ChatRoomVo> queryAllChatRoomList(){
        List<ChatRoomVo> chatRoomList = new ArrayList<>();
        ChatRoomVo chatRoom = null;
        SQLiteDatabase   db = null;
        Cursor       cursor = null;
        try{
            db = helper.getWritableDatabase();
            cursor = db.rawQuery("select * from " + DBOpenHelper.CHAT_ROOM_TBL + " order by end_time desc ", null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    chatRoom = ChatRoomFactory.getInstance().buildChatRoomByCursor(cursor);
                    chatRoomList.add(chatRoom);
                }
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryAllChatRoomList->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(cursor != null)
                cursor.close();

            if(db != null)
                db.close();
        }
        return chatRoomList;
    }

    /**
     * 查询所有显示在消息中心聊天室记录表
     * @return
     */
    public ChatRoomVo queryChatRoomBySessionId(String sessionId){
        ChatRoomVo chatRoom = null;
        SQLiteDatabase   db = null;
        Cursor       cursor = null;
        try{
            db = helper.getWritableDatabase();
            cursor = db.query(DBOpenHelper.CHAT_ROOM_TBL, null, " session_id = ? ",
                    new String[]{sessionId}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    chatRoom = ChatRoomFactory.getInstance().buildChatRoomByCursor(cursor);
                }
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryAllChatRoomList->" + e.getMessage());
            e.printStackTrace();
        } finally {
            if(cursor != null)
                cursor.close();

            if(db != null)
                db.close();
        }
        return chatRoom;
    }

    /**
     * 删除所有聊天室
     * @return
     */
    public long deleteAll(){
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.delete(DBOpenHelper.CHAT_ROOM_TBL, "1 = ?", new String[]{"1"});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+" deleteAll->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    public boolean isChatRoomExistsBySessionID(String sessionID ) {
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query(DBOpenHelper.CHAT_ROOM_TBL, null, " session_id = ? ",
                    new String[] { sessionID }, null, null, null);
            if(cursor.getCount() > 0){
                return true;
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".isChatRoomExistsBySessionID->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();

            if(null != cursor)
                cursor.close();
        }
        return false;
    }

    public long deleteChatRoomBySessionID(String sessionID) {
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.delete(DBOpenHelper.CHAT_ROOM_TBL, "session_id = ?", new String[]{sessionID});
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG+" deleteChatRoomBySessionID->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }
}
