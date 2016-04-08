package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.factory.ComingFactory;
import com.zkjinshi.superservice.vo.ComingVo;

import java.util.ArrayList;

/**
 * 区域信息表操作工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ComingDBUtil {

    private final static String TAG = ComingDBUtil.class.getSimpleName();

    private Context      context;
    private DBOpenHelper helper;
    private static ComingDBUtil instance;

    private ComingDBUtil(){
    }

    public synchronized static ComingDBUtil getInstance(){
        if(null == instance){
            instance = new ComingDBUtil();
        }
        instance.init();
        return instance;
    }

    private void init() {
        context = ServiceApplication.getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 插入单个到店信息
     * @param comingVo
     * @return
     */
    public long addComing(ComingVo comingVo) {
        ContentValues values = ComingFactory.getInstance().buildContentValues(comingVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            id = db.insert(DBOpenHelper.COMING_TBL, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != db)
                db.close();
        }
        return id;
    }

    /**
     * 获取单页到店通知
     * @param lastSendTime
     * @param limitSize
     * @param isInclude
     * @return
     */
    public ArrayList<ComingVo> queryComingList(long lastSendTime, int limitSize, boolean isInclude){
        ArrayList<ComingVo> comingList = new ArrayList<ComingVo>();
        ComingVo comingVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                if(isInclude){
                    cursor = db.query(DBOpenHelper.COMING_TBL, null,
                            " arrive_time <= ? ",
                            new String[] {"" + lastSendTime },
                            null, null, " arrive_time desc", "" + limitSize);
                }else{
                    cursor = db.query(DBOpenHelper.COMING_TBL, null,
                            "  arrive_time < ? ",
                            new String[] {"" + lastSendTime },
                            null, null, " arrive_time desc", "" + limitSize);
                }
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        comingVo = ComingFactory.getInstance().buildComing(cursor);
                        comingList.add(comingVo);
                    }
                }
            } catch (Exception e) {
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
        return  comingList;
    }

}
