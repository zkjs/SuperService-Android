package com.zkjinshi.superservice.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;

/**
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DBOpenHelper extends SQLiteOpenHelper {


    public static String DB_NAME = "super_service.db";//根据每个用户创建一份数据库
    public static final int VERSION = 2;// 数据库版本
    public static final String MESSAGE_TBL = "messagetbl";// 消息表名
    public static final String CHAT_ROOM_TBL = "chatroomtbl";//聊天室表名
    public static final String MEMBER_TBL = "membertbl";//聊天室成员表
    public static final String USER_TBL = "user_tbl";// 客户表名
    public static final String CLIENT_TBL = "client_tbl";// 客户表名
    public static final String COMING_TBL = "coming_tbl";//到店信息表
    public static final String SHOP_EMPLOYEE_TBL = "shop_employee_tbl";//商家员工列表
    public static final String SHOP_DEPARTMENT_TBL = "shop_department_tbl";//商家部门表

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 创建数据库时调用
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        ORMOpenHelper.createTables(db);
    }

    /**
     * 当数据库更新时调用
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.getInstance().info(LogLevel.DEBUG, "【oldVersion】" + oldVersion);
        LogUtil.getInstance().info(LogLevel.DEBUG,"【newVersion】"+oldVersion);
        ORMOpenHelper.upgradeTables(db, TableOpenHelper.getTableNames());
    }

}
