package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.bean.LatestClientBean;
import com.zkjinshi.superservice.entity.MsgPushTriggerLocNotificationM2S;
import com.zkjinshi.superservice.factory.LatestClientFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 到店通知工具类
 * 开发者：vincent
 * 日期：2015/9/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LatestClientDBUtil {

    private final static String TAG = LatestClientDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper helper;

    private static LatestClientDBUtil instance = null;

    private LatestClientDBUtil(){
    }

    public synchronized static LatestClientDBUtil getInstance(){
        if(instance == null){
            instance = new LatestClientDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = ServiceApplication.getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 根据到店用户消息添加
     * @param locNotificationM2S
     * @return
     */
    public long addLatestClient(MsgPushTriggerLocNotificationM2S locNotificationM2S) {
        ContentValues values = LatestClientFactory.getInstance().buildContentValues(
                                                                 locNotificationM2S);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.CLIENT_LATEST_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addLatestClient->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return id;
    }

    /**
     * 查询
     * @return
     */
    public LatestClientBean queryLatestClientByUserID(String userID) {
        LatestClientBean client = null;
        SQLiteDatabase db   = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();

                String sql = " select * from " + DBOpenHelper.CLIENT_LATEST_TBL + " as c where c.timestamp in ( "
                        +" select MAX(timestamp) from "
                        + DBOpenHelper.CLIENT_LATEST_TBL + " as c2 where c2.user_id = " + userID
                        + " ) ";

                cursor = db.rawQuery(sql, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        client = LatestClientFactory.getInstance().bulidLatestClient(cursor);
                    }
                    LogUtil.getInstance().info(LogLevel.INFO, TAG + ".queryLatestClientByUserID 成功" + client.getUserID());
                } else {
                    LogUtil.getInstance().info(LogLevel.INFO, TAG + ".queryLatestClientByUserID为空");
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryLatestClientByUserID->"+e.getMessage());
                e.printStackTrace();
            } finally {

                if (null != cursor) {
                    cursor.close();
                }

                if (null != db) {
                    db.close();
                }
            }
        }
        return client;
    }

    /**
     * 查询最近10条到店消息记录
     * @return
     */
    public List<LatestClientBean> queryLatestClients() {
        ArrayList<LatestClientBean> clientList = new ArrayList<>();
        LatestClientBean client = null;
        SQLiteDatabase db   = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();

                String sql = " select * from " + DBOpenHelper.CLIENT_LATEST_TBL + " as c where c.timestamp in ( "
                        +" select MAX(timestamp) from "
                        + DBOpenHelper.CLIENT_LATEST_TBL + " as c2 where c2.user_id in ( "
                        + " select distinct user_id from " + DBOpenHelper.CLIENT_LATEST_TBL
                        + " ) "
                        + " ) ";

                cursor = db.rawQuery(sql, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        client = LatestClientFactory.getInstance().bulidLatestClient(cursor);
                        clientList.add(client);
                    }
                    LogUtil.getInstance().info(LogLevel.INFO, TAG + ".获取最近10位到店用户成功" + clientList);
                } else {
                    LogUtil.getInstance().info(LogLevel.INFO, TAG + ".获取到店用户失败或者到店用户为空");
                }

            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR,TAG+".queryLatestClients->"+e.getMessage());
                e.printStackTrace();
            } finally {

                if (null != cursor) {
                    cursor.close();
                }

                if (null != db) {
                    db.close();
                }
            }
        }
        return clientList;
    }
}
