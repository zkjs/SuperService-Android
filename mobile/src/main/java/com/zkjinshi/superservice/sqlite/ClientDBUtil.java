package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.factory.ClientFactory;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.ContactType;

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

    public long addClient(ClientVo clientVo){
        ContentValues values = ClientFactory.getInstance().buildAddContentValues(clientVo);
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
     * 更新非正式客户
     * @param client
     * @return
     */
    public long updateClient(ClientVo client){
        ContentValues values = ClientFactory.getInstance().buildUpdateContentValues(client);
        long addResult    = -1;
        SQLiteDatabase db = null;
        try {
            db        = helper.getWritableDatabase();
            addResult = db.insert(DBOpenHelper.CLIENT_TBL, null, values);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".updateClient->" + e.getMessage());
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
    public List<ClientVo> queryAll() {
        List<ClientVo> clientList = new ArrayList<>();
        ClientVo clientVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.CLIENT_TBL, null, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        clientVo = ClientFactory.getInstance().buildclient(cursor);
                        clientList.add(clientVo);
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

    /**
     * 根据phone判断客户是否存在
     * @return
     */
    public Boolean isClientExistByPhone(String phone) {
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.rawQuery(" select * from " + DBOpenHelper.CLIENT_TBL + " where phone = ? ", new String[] { phone });
            if(cursor.getCount() > 0){
                return true;
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".isClientExistByPhone->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();

            if(null != cursor)
                cursor.close();
        }
        return false;
    }

    public List<ClientVo> queryUnNormalClient() {
        List<ClientVo> clientList = new ArrayList<>();
        ClientVo clientVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.CLIENT_TBL, null, " contact_type = ? ",
                                  new String[] { ContactType.UNNORMAL.getValue()+"" },
                                  null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        clientVo = ClientFactory.getInstance().buildclient(cursor);
                        clientList.add(clientVo);
                    }
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR, TAG+".queryUnNormalClient->"+e.getMessage());
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
     * 根据手机号查询客户对象
     * @param phone
     * @return
     */
    public ClientVo findClientByPhone(String phone) {
        ClientVo clientVo = null;
        SQLiteDatabase   db       = null;
        Cursor       cursor       = null;
        try{
            db = helper.getWritableDatabase();
            cursor = db.query(DBOpenHelper.UNREG_CLIENT_TBL, null, " phone = ? ",
                    new String[]{ phone }, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    clientVo = ClientFactory.getInstance().buildclient(cursor);
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
        return clientVo;
    }

    /**
     * 根据客户ID查询头像背景颜色值
     * @param clientID
     * @return
     */
    public int queryBgDrawableResByClientID(String clientID) {
        int    bgColorRes = 0;
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query(DBOpenHelper.CLIENT_TBL,
                    new String[]{" bg_drawable_res "},
                    " userid = ? ",
                    new String[] { clientID },
                    null, null, null);

            if(null != cursor && cursor.getCount() > 0){
                bgColorRes = cursor.getInt(0);
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryBgDrawableResByClientID->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();

            if(null != cursor)
                cursor.close();
        }
        return bgColorRes;
    }

    /**
     * 根据客户ID更新客户背景值
     * @param clientID
     * @param bgRes
     */
    public long updateClientBgDrawableResByClientID(String clientID, int bgRes) {
        long updateReslut = -1;
        SQLiteDatabase db = null;
        ContentValues values = null;
        try {
            db        = helper.getWritableDatabase();
            values    = new ContentValues();

            values.put("bg_drawable_res", bgRes);
            updateReslut = db.update(DBOpenHelper.CLIENT_TBL, values, " userid = ? ", new String[] { clientID });
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".updateClientBgDrawabelResByClientID->" + e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return updateReslut;
    }
}