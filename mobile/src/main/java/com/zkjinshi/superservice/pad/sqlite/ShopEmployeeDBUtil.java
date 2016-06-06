package com.zkjinshi.superservice.pad.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.pad.ServiceApplication;
import com.zkjinshi.superservice.pad.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.pad.vo.EmployeeVo;


import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopEmployeeDBUtil {

    private final static String TAG = ShopEmployeeDBUtil.class.getSimpleName();

    private Context mContext;
    private DBOpenHelper helper;

    private static ShopEmployeeDBUtil instance = null;

    private ShopEmployeeDBUtil(){
    }

    public synchronized static ShopEmployeeDBUtil getInstance(){
        if(instance == null){
            instance = new ShopEmployeeDBUtil();
        }
        instance.init();
        return instance;
    }

    private void init() {
        mContext = ServiceApplication.getContext();
        helper   = new DBOpenHelper(mContext);
    }



    public long updateShopEmployee(EmployeeVo EmployeeVo){
        ContentValues values = ShopEmployeeFactory.getInstance().buildContentValues(EmployeeVo);
        long updateResult    = -1;
        SQLiteDatabase db = null;
        try {
            db        = helper.getWritableDatabase();
            updateResult = db.update(DBOpenHelper.SHOP_EMPLOYEE_TBL, values, " userid = ?", new String[] { EmployeeVo.getUserid() });
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".updateShopEmployee->" + e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return updateResult;
    }

    /**
     * @param EmployeeVos
     * @return
     */
    public long batchAddShopEmployees(List<EmployeeVo> EmployeeVos){
        long addResult    = -1;
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            db.beginTransaction();
            for(EmployeeVo EmployeeVo : EmployeeVos){
                ContentValues values = ShopEmployeeFactory.getInstance().buildContentValues(EmployeeVo);
                try{
                    addResult = db.insert(DBOpenHelper.SHOP_EMPLOYEE_TBL, null, values);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(addResult == -1){
                    String empID = EmployeeVo.getUserid();
                    addResult = db.update(DBOpenHelper.SHOP_EMPLOYEE_TBL, values, " userid = ? ", new String[]{ empID });
                }
            }

        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".batchAddShopEmployees->" + e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db){
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
            }
        }
        return addResult;
    }
    /**
     * query by department id asc
     * @return
     */
    public ArrayList<EmployeeVo> queryAllExceptUser(String userID) {
        ArrayList<EmployeeVo> employeeVos = new ArrayList<EmployeeVo>();
        EmployeeVo employeeVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.SHOP_EMPLOYEE_TBL, null, " userid != ?", new String[] {userID}, null, null, " rolename ASC");
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        employeeVo = ShopEmployeeFactory.getInstance().bulidEmployeeVo(cursor);
                        employeeVos.add(employeeVo);
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
        return  employeeVos;
    }



    /**
     * 批量删除数据库员工
     * @param empIDList
     * @return
     */
    public long deleteShopEmployeeByEmpIDs(List<String> empIDList) {
        SQLiteDatabase db = null;
        long delResult = 0;
        try {
            db = helper.getWritableDatabase();
            db.beginTransaction();
            for(String empID : empIDList){
                delResult += db.delete(DBOpenHelper.SHOP_EMPLOYEE_TBL, " userid = ? ", new String[] { empID });
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".deleteShopEmployeeByEmpID->"+e.getMessage());
            e.printStackTrace();
        }finally{
            if(null != db) {
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
            }
        }
        return delResult;
    }

    /**
     * 删除数据库员工
     * @return
     */
    public long deleteAllShopEmployee() {
        SQLiteDatabase db = null;
        long delResult = 0;
        try {
            db = helper.getWritableDatabase();
            db.beginTransaction();
            delResult += db.delete(DBOpenHelper.SHOP_EMPLOYEE_TBL, null, null);
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".deleteAllShopEmployee->"+e.getMessage());
            e.printStackTrace();
        }finally{
            if(null != db) {
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
            }
        }
        return delResult;
    }

    public EmployeeVo queryEmployeeById( String empId) {
        EmployeeVo EmployeeVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.SHOP_EMPLOYEE_TBL, null,
                        "  userid = ? ", new String[] { empId}, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        EmployeeVo = new EmployeeVo();
                        EmployeeVo =  ShopEmployeeFactory.getInstance().bulidEmployeeVo(cursor);
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
        return EmployeeVo;
    }




}
