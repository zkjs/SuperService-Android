package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.bean.ZoneBean;
import com.zkjinshi.superservice.entity.MsgCustomerServiceImgChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceMediaChat;
import com.zkjinshi.superservice.entity.MsgCustomerServiceTextChat;
import com.zkjinshi.superservice.factory.ChatRoomFactory;
import com.zkjinshi.superservice.factory.MessageFactory;
import com.zkjinshi.superservice.factory.ZoneFactory;
import com.zkjinshi.superservice.vo.ChatRoomVo;
import com.zkjinshi.superservice.vo.ZoneVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 区域信息表操作工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneDBUtil {

    private final static String TAG = ZoneDBUtil.class.getSimpleName();

    private Context      context;
    private DBOpenHelper helper;
    private static ZoneDBUtil instance;

    private ZoneDBUtil(){
    }

    public synchronized static ZoneDBUtil getInstance(){
        if(null == instance){
            instance = new ZoneDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = ServiceApplication.getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 插入区域信息
     * @param zoneBean
     * @return
     */
    public long addZone(ZoneBean zoneBean) {
        ContentValues values = ZoneFactory.getInstance().buildContentValues(zoneBean);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            try {
                db = helper.getWritableDatabase();
                id = db.insert(DBOpenHelper.ZONE_TBL, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (id == -1) {
                id = db.update(DBOpenHelper.ZONE_TBL,
                        values, " loc_id = ? ",
                        new String[]{""+zoneBean.getLocid()});
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
     * 批量插入区域信息
     * @return
     */
    public long batchAddZone(ArrayList<ZoneBean> zoneBeanList){
        SQLiteDatabase database = null;
        ContentValues values = null;
        ZoneBean zoneBean = null;
        int locId = 0;
        long id = -1;
        try {
            database = helper.getWritableDatabase();
            database.beginTransaction();
            for (ZoneBean zone : zoneBeanList) {
                values = ZoneFactory.getInstance().buildContentValues(zone);
                try {
                    id = database.insert(DBOpenHelper.ZONE_TBL, null,
                            values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(id == -1){
                    locId = zone.getLocid();
                    id = database.update(DBOpenHelper.ZONE_TBL, values, " loc_id = ? ", new String[]{""+locId});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(null != database){
                database.setTransactionSuccessful();
                database.endTransaction();
                database.close();
            }
        }
        return id;
    }

    /**
     * 查询聊天室信息
     * @param locId
     * @return
     */
    public ZoneVo queryZoneByLocId(String locId){
        ZoneVo zoneVo = null;
        SQLiteDatabase   db = null;
        Cursor       cursor = null;
        try{
            db = helper.getWritableDatabase();
            cursor = db.query(DBOpenHelper.ZONE_TBL, null, " loc_id = ? ",
                    new String[]{locId}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    zoneVo = ZoneFactory.getInstance().buildZone(cursor);
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
        return zoneVo;
    }


    /**
     * 查询聊天室信息
     * @param shopId
     * @return
     */
    public ArrayList<ZoneVo> queryZoneListByShopId(String shopId){
        ArrayList<ZoneVo> zoneList = new ArrayList<ZoneVo>();
        ZoneVo zoneVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try{
            db = helper.getWritableDatabase();
            cursor = db.query(DBOpenHelper.ZONE_TBL, null, " shop_id = ? ",
                    new String[]{shopId}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    zoneVo = ZoneFactory.getInstance().buildZone(cursor);
                    zoneList.add(zoneVo);
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
        return zoneList;
    }

}
