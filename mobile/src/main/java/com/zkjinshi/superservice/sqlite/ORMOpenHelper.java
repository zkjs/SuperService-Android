package com.zkjinshi.superservice.sqlite;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ORMOpenHelper {

    /**
     * 创建数据库表
     * @param db
     */
    public static void createTables(SQLiteDatabase db) {
        db.execSQL(TableOpenHelper.CLIENT_TBL_SQL);
        db.execSQL(TableOpenHelper.SHOP_EMPLOYEE_TBL_SQL);
        db.execSQL(TableOpenHelper.SHOP_DEPARTMENT_TBL_SQL);
    }

    /**
     * 升级数据库表
     * @param db
     * @param tableNames
     */
    public static void upgradeTables(SQLiteDatabase db, String[] tableNames) {
        try {
            String tempTableName = null;
            String sql = null;
            String columns = null;
            db.beginTransaction();
            // 1、重命名所有表
            for (String tableName : tableNames) {
                tempTableName = "temp_" + tableName;
                sql = "ALTER TABLE " + tableName + " RENAME TO "
                        + tempTableName;
                db.execSQL(sql);
            }
            // 2、创建新表
            createTables(db);
            // 3、拷贝数据
            for (String tableName : tableNames) {
                tempTableName = "temp_" + tableName;
                columns = getColumnNames(db, tempTableName);
                sql = "INSERT INTO " + tableName + " (" + columns + ") "
                        + " SELECT " + columns + " FROM " + tempTableName;
                db.execSQL(sql);
            }
            // 4、删除临时表
            for (String tableName : tableNames) {
                tempTableName = "temp_" + tableName;
                sql = "DROP TABLE IF EXISTS " + tempTableName;
                db.execSQL(sql);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 获取表字段分隔字符
     * @param db
     * @param tableName
     * @return
     */
    public static String getColumnNames(SQLiteDatabase db, String tableName) {
        StringBuffer columnNames = null;
        Cursor cursor = null;
        try {
            columnNames = new StringBuffer();
            cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            if (null != cursor) {
                int columnIndex = cursor.getColumnIndex("name");
                if (-1 == columnIndex) {
                    return null;
                }
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                        .moveToNext()) {
                    columnNames.append(cursor.getString(columnIndex) + ",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return columnNames.deleteCharAt(columnNames.length() - 1).toString();
    }
}
