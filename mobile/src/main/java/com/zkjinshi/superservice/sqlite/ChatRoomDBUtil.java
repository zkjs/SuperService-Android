package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.entity.MsgCustomerServiceImgChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceMediaChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceTextChat;
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
     * 插入聊天室
     * @param msgText
     * @return
     */
    public long addChatRoom(MsgCustomerServiceTextChat msgText) {
        ContentValues values = ChatRoomFactory.getInstance().buildContentValues(msgText);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            try {
                db = helper.getWritableDatabase();
                id = db.insert(DBOpenHelper.CHAT_ROOM_TBL, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (id == -1) {
                id = db.update(DBOpenHelper.CHAT_ROOM_TBL,
                        values, " chat_id = ? ",
                        new String[]{msgText.getSessionid()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != db)
                db.close();
        }
        return id;
    }

    public long addChatRoom(MsgCustomerServiceImgChat msgImg) {
        ContentValues values = ChatRoomFactory.getInstance().buildContentValues(msgImg);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            try {
                db = helper.getWritableDatabase();
                id = db.insert(DBOpenHelper.CHAT_ROOM_TBL, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (id == -1) {
                id = db.update(DBOpenHelper.CHAT_ROOM_TBL,
                        values, " chat_id = ? ",
                        new String[]{msgImg.getSessionid()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != db)
                db.close();
        }
        return id;
    }

    public long addChatRoom(MsgCustomerServiceMediaChat msgMedia) {
        ContentValues values = ChatRoomFactory.getInstance().buildContentValues(msgMedia);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            try {
                db = helper.getWritableDatabase();
                id = db.insert(DBOpenHelper.CHAT_ROOM_TBL, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (id == -1) {
                id = db.update(DBOpenHelper.CHAT_ROOM_TBL,
                        values, " chat_id = ? ",
                        new String[]{msgMedia.getSessionid()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != db)
                db.close();
        }
        return id;
    }

    /**
     * 查询聊天室信息
     * @param sessionId
     * @return
     */
    public ChatRoomVo queryChatRoomBySessionId(String sessionId){
        ChatRoomVo chatRoom = null;
        SQLiteDatabase   db = null;
        Cursor       cursor = null;
        try{
            db = helper.getWritableDatabase();
            cursor = db.query(DBOpenHelper.CHAT_ROOM_TBL, null, " chat_id = ? ",
                    new String[]{sessionId}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    chatRoom = ChatRoomFactory.getInstance().buildChatRoomVo(cursor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null)
                cursor.close();

            if(db != null)
                db.close();
        }
        return chatRoom;
    }

}
