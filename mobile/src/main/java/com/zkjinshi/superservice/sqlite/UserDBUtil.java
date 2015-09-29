package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.factory.UserFactory;
import com.zkjinshi.superservice.vo.UserVo;

/**
 * 开发者：vincent
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UserDBUtil {

    private final static String TAG = UserDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper    helper;
    private static UserDBUtil instance;

    private UserDBUtil(){
    }

    public synchronized static UserDBUtil getInstance(){
        if(null == instance){
            instance = new UserDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = ServiceApplication.getContext();
        helper  = new DBOpenHelper(context);
    }

    /**
     * 添加用户详细信息
     * @param userVo
     * @return
     */
    public long addUser(UserVo userVo) {

        ContentValues values = UserFactory.getInstance().buildContentValues(userVo);
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();

            try {
                id = db.insert(DBOpenHelper.USER_TBL, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(id == -1){
                id = db.update(DBOpenHelper.USER_TBL, values, " user_id = ? ", new String[]{userVo.getUserId()});
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(null != db)
            db.close();
        }
        return id;
    }

    /**
     * 根据userId查询用户信息
     * @param userId
     * @return
     */
    public UserVo queryUserById(String userId) {
        Cursor cursor = null;
        SQLiteDatabase db = null;
        UserVo userVo = null;
        try {
            db = helper.getWritableDatabase();
            cursor = db.rawQuery("select * from " + DBOpenHelper.USER_TBL +
                    " where user_id = ?", new String[]{userId});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    userVo = UserFactory.getInstance().buildUserVo(cursor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if (null != cursor)
                cursor.close();
            if (null != db)
                db.close();
        }
        return userVo;
    }

}
