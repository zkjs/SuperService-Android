package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

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
            instance.init();
        }
        return instance;
    }

    private void init() {
        mContext = ServiceApplication.getContext();
        helper   = new DBOpenHelper(mContext);
    }

    public long addShopEmployee(ShopEmployeeVo shopEmployeeVo){
        ContentValues values = ShopEmployeeFactory.getInstance().buildAddContentValues(shopEmployeeVo);
        long addResult    = -1;
        SQLiteDatabase db = null;
        try {
            db        = helper.getWritableDatabase();
            addResult = db.insert(DBOpenHelper.SHOP_EMPLOYEE_TBL, null, values);
            if(addResult == -1){
                db.update(DBOpenHelper.SHOP_EMPLOYEE_TBL, values, " empid = ?", new String[] {shopEmployeeVo.getEmpid()});
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addShopEmployee->" + e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return addResult;
    }

    /**
     * @param shopEmployeeVos
     * @return
     */
    public long batchAddShopEmployees(List<ShopEmployeeVo> shopEmployeeVos){
        long addResult    = -1;
        SQLiteDatabase db = null;
        try {
            db        = helper.getWritableDatabase();
            db.beginTransaction();
            for(ShopEmployeeVo shopEmployeeVo : shopEmployeeVos){
                ContentValues values = ShopEmployeeFactory.getInstance().buildAddContentValues(shopEmployeeVo);
                try {
                    addResult = db.insert(DBOpenHelper.SHOP_EMPLOYEE_TBL, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(addResult == -1){
                    String empID = shopEmployeeVo.getEmpid();
                    addResult = db.update(DBOpenHelper.SHOP_EMPLOYEE_TBL, values, " empid = ? ", new String[]{ empID });
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
     * 查询所有团队联系人
     * @return
     */
    public List<ShopEmployeeVo> queryAll() {
        List<ShopEmployeeVo> shopEmployeeVos = new ArrayList<>();
        ShopEmployeeVo shopEmployeeVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.SHOP_EMPLOYEE_TBL, null, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        shopEmployeeVo = ShopEmployeeFactory.getInstance().buildShopEmployee(cursor);
                        shopEmployeeVos.add(shopEmployeeVo);
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
        return  shopEmployeeVos;
    }

}
