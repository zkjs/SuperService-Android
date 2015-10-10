package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.bean.ClientBean;
import com.zkjinshi.superservice.factory.ClientFactory;
import com.zkjinshi.superservice.vo.ClientVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientDBUtil {

    private final static String TAG = ClientDBUtil.class.getSimpleName();

    private Context      mContext;
    private DBOpenHelper helper;

    private static ClientDBUtil instance = null;

    private ClientDBUtil(){
    }

    public synchronized static ClientDBUtil getInstance(){
        if(instance == null){
            instance = new ClientDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        mContext = ServiceApplication.getContext();
        helper   = new DBOpenHelper(mContext);
    }

    public long addClient(ClientBean client){
        ContentValues values = ClientFactory.getInstance().buildAddContentValues(client);
        long addResult    = -1;
        SQLiteDatabase db = null;
        try {
            db        = helper.getWritableDatabase();
            addResult = db.insert(DBOpenHelper.CLIENT_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addClient->" + e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return addResult;
    }

    /**
     * 获取当前所有
     */
    public List<ClientBean> queryAll() {
        List<ClientBean> clientList = new ArrayList<>();
        ClientBean client = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.CLIENT_TBL, null, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        client = ClientFactory.getInstance().buildclient(cursor);
                        clientList.add(client);
                    }
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR, TAG+".queryAll->"+e.getMessage());
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
        return  clientList;
    }

    /**
     * 根据UserID判断客户是否存在
     * @return
     */
    public Boolean isClientExistByUserID(String userID) {
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.rawQuery(" select * from " + DBOpenHelper.CLIENT_TBL + " where userid = ? ", new String[] { userID });
            if(cursor.getCount() > 0){
                return true;
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".isClientExistByUserID->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();

            if(null != cursor)
                cursor.close();
        }
        return false;
    }
}
