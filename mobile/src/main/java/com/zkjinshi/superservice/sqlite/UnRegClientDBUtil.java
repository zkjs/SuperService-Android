package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.factory.UnRegClientFactory;
import com.zkjinshi.superservice.vo.UnRegClientVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/10/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UnRegClientDBUtil {

    private final static String TAG = UnRegClientDBUtil.class.getSimpleName();

    private Context      mContext;
    private DBOpenHelper helper;

    private static UnRegClientDBUtil instance = null;

    private UnRegClientDBUtil(){
    }

    public synchronized static UnRegClientDBUtil getInstance(){
        if(instance == null){
            instance = new UnRegClientDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        mContext = ServiceApplication.getContext();
        helper   = new DBOpenHelper(mContext);
    }

    /**
     * 添加非正式客户
     * @param unRegClient
     * @return
     */
    public long addUnRegClient(UnRegClientVo unRegClient){
        ContentValues values = UnRegClientFactory.getInstance().buildAddContentValues(unRegClient);
        long addResult    = -1;
        SQLiteDatabase db = null;
        try {
            db        = helper.getWritableDatabase();
            addResult = db.insert(DBOpenHelper.UNREG_CLIENT_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addUnRegClient->" + e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return addResult;
    }

    /**
     * 更新非正式客户
     * @param unRegClient
     * @return
     */
    public long updateUnRegClient(UnRegClientVo unRegClient){
        ContentValues values = UnRegClientFactory.getInstance().buildUpdateContentValues(unRegClient);
        long addResult    = -1;
        SQLiteDatabase db = null;
        try {
            db        = helper.getWritableDatabase();
            addResult = db.insert(DBOpenHelper.UNREG_CLIENT_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addUnRegClient->" + e.getMessage());
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
    public List<UnRegClientVo> queryAll() {
        List<UnRegClientVo> unRegClients = new ArrayList<>();
        UnRegClientVo unRegClient = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.UNREG_CLIENT_TBL, null, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        unRegClient = UnRegClientFactory.getInstance().buildUnRegClient(cursor);
                        unRegClients.add(unRegClient);
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
        return unRegClients;
    }

    /**
     * 根据phone判断客户是否存在
     * @return
     */
    public Boolean isUnRegClientExistByPhone(String phone) {
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.rawQuery(" select * from " + DBOpenHelper.UNREG_CLIENT_TBL + " where phone = ? ", new String[] { phone });
            if(cursor.getCount() > 0){
                return true;
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".isUnRegClientExistByPhone->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();

            if(null != cursor)
                cursor.close();
        }
        return false;
    }

    /**
     * 根据手机号查找客户
     * @param phone
     */
    public UnRegClientVo findUnRegClientByPhone(String phone) {
        UnRegClientVo unRegClient = null;
        SQLiteDatabase   db       = null;
        Cursor       cursor       = null;
        try{
            db = helper.getWritableDatabase();
            cursor = db.query(DBOpenHelper.UNREG_CLIENT_TBL, null, " phone = ? ",
                              new String[]{ phone }, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    unRegClient = UnRegClientFactory.getInstance().buildUnRegClient(cursor);
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
        return unRegClient;
    }
}
