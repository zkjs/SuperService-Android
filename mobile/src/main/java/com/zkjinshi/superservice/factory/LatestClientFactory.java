package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.database.Cursor;

import com.zkjinshi.superservice.vo.LatestClientVo;
import com.zkjinshi.superservice.entity.MsgPushTriggerLocNotificationM2S;

/**
 * 到店客户信息数据工厂类
 * 开发者：vincent
 * 日期：2015/9/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LatestClientFactory {

    private static LatestClientFactory instance = null;

    private LatestClientFactory(){
    }

    public synchronized static LatestClientFactory getInstance(){
        if(instance == null){
            instance = new LatestClientFactory();
        }
        return instance;
    }

    /**
     * 根据到店通知生成到店用户
     * @param locNotificationM2S
     * @return
     */
    public LatestClientVo buildClientByMsgLoc(MsgPushTriggerLocNotificationM2S locNotificationM2S) {
        LatestClientVo client = new LatestClientVo();
        client.setUserID(locNotificationM2S.getUserid());
        client.setUserName(locNotificationM2S.getUsername());
        client.setTimeStamp(locNotificationM2S.getTimestamp());
        client.setShopID(locNotificationM2S.getShopid());
        client.setLocID(locNotificationM2S.getLocid());
        return client;
    }

    /**
     * bulid contentValues for insert client
     * @param locNotificationM2S
     * @return
     */
    public ContentValues buildContentValues(MsgPushTriggerLocNotificationM2S locNotificationM2S) {
        LatestClientVo client = buildClientByMsgLoc(locNotificationM2S);
        ContentValues values = new ContentValues();
        values.put("user_id", client.getUserID());
        values.put("user_name", client.getUserName());
        values.put("timestamp", client.getTimeStamp());
        values.put("shop_id", client.getShopID());
        values.put("loc_id", client.getLocID());
        return values;
    }

    /**
     * 根据Cursor生成
     * @param cursor
     * @return
     */
    public LatestClientVo bulidLatestClient(Cursor cursor) {
        LatestClientVo client = new LatestClientVo();
        client.setUserID(cursor.getString(0));
        client.setUserName(cursor.getString(1));
        client.setTimeStamp(cursor.getLong(2));
        client.setShopID(cursor.getString(3));
        client.setLocID(cursor.getString(4));
        return client;
    }
}
