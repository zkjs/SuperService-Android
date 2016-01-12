package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.utils.CacheUtil;
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
            Cursor cursor = db.rawQuery(" select * from "
                            + DBOpenHelper.SHOP_EMPLOYEE_TBL
                            + " where empid = ? ",
                                new String[] { shopEmployeeVo.getEmpid() });

            if(cursor.getCount() > 0){
                addResult = db.update(DBOpenHelper.SHOP_EMPLOYEE_TBL, values, " empid = ?", new String[] { shopEmployeeVo.getEmpid() });
            } else {
                addResult = db.insert(DBOpenHelper.SHOP_EMPLOYEE_TBL, null, values);
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

    public long updateShopEmployee(ShopEmployeeVo shopEmployeeVo){
        ContentValues values = ShopEmployeeFactory.getInstance().buildUpdateContentValues(shopEmployeeVo);
        long updateResult    = -1;
        SQLiteDatabase db = null;
        try {
            db        = helper.getWritableDatabase();
            updateResult = db.update(DBOpenHelper.SHOP_EMPLOYEE_TBL, values, " empid = ?", new String[] { shopEmployeeVo.getEmpid() });
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

    /**
     * 获取员工总个数
     * @param shopId
     * @return
     */
    public int queryTotalEmpCount(String shopId) {
        int totalEmpCount = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.SHOP_EMPLOYEE_TBL, null,
                        " shop_id = ? ",
                        new String[]{shopId}, null, null, null);
                totalEmpCount = cursor.getCount();
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
        return totalEmpCount;
    }

    /**
     * 根据empID判断员工是否存在
     * @return
     */
    public Boolean isEmployeeExistByEmpID(String empID) {
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.rawQuery(" select * from " + DBOpenHelper.SHOP_EMPLOYEE_TBL + " where empid = ? ", new String[]{empID});
            if(cursor.getCount() > 0){
                return true;
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".isEmployeeExistByEmpID->"+e.getMessage());
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
     * 根据empID查询默认背景颜色资源值
     * @return
     */
    public int queryBgColorResByEmpID(String empID) {
        int    bgColorRes = 0;
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query(DBOpenHelper.SHOP_EMPLOYEE_TBL,
                    new String[]{" bg_color_res "},
                    " empid = ? ",
                    new String[]{empID},
                    null, null, null);
            if(cursor.moveToFirst()){
                bgColorRes = cursor.getInt(0);
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryBgColorResByEmpID->"+e.getMessage());
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
     * query by department id asc
     * @return
     */
    public List<ShopEmployeeVo> queryAllByDeptIDAsc() {
        List<ShopEmployeeVo> shopEmployeeVos = new ArrayList<>();
        ShopEmployeeVo shopEmployeeVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.SHOP_EMPLOYEE_TBL, null, null, null, null, null, " dept_id ASC");
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

    /**
     * query by department id asc
     * @return
     */
    public List<ShopEmployeeVo> queryAllExceptUser(String userID) {
        List<ShopEmployeeVo> shopEmployeeVos = new ArrayList<>();
        ShopEmployeeVo shopEmployeeVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.SHOP_EMPLOYEE_TBL, null, null, null, null, null, " dept_id ASC");
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        shopEmployeeVo = ShopEmployeeFactory.getInstance().buildShopEmployee(cursor);
                        if(!shopEmployeeVo.getEmpid().equals(userID)){
                            shopEmployeeVos.add(shopEmployeeVo);
                        }
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

    /**
     * 根据员工ID进行本地数据库的删除
     * @param empid
     */
    public long deleteShopEmployeeByEmpID(String empid) {
        SQLiteDatabase db = null;
        long delResult = -1;
        try {
            db = helper.getWritableDatabase();
            delResult = db.delete(DBOpenHelper.SHOP_EMPLOYEE_TBL, " empid = ? ", new String[]{ empid });
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR,TAG+".deleteShopEmployeeByEmpID->"+e.getMessage());
            e.printStackTrace();
        }finally{
            if(null != db)
                db.close();
        }
        return delResult;
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
                delResult += db.delete(DBOpenHelper.SHOP_EMPLOYEE_TBL, " empid = ? ", new String[] { empID });
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

    public ShopEmployeeVo queryEmployeeById(String empId) {
        ShopEmployeeVo shopEmployeeVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.SHOP_EMPLOYEE_TBL, null,
                        " empid = ? ", new String[] { empId}, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        shopEmployeeVo = new ShopEmployeeVo();
                        shopEmployeeVo =  ShopEmployeeFactory.getInstance().buildShopEmployee(cursor);
                    }
                }

            } catch (Exception e) {
                // TODO: handle exception
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
        return shopEmployeeVo;
    }

    /**
     * 查询员工最后一次服务器在线时间
     * @param empID
     * @return
     */
    public long queryLatestOnlineByEmpID(String empID) {
        long    lateOnlineTime = 0;
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query(DBOpenHelper.SHOP_EMPLOYEE_TBL,
                    new String[]{" last_online_time "},
                    " empid = ? ",
                    new String[]{empID},
                    null, null, null);
            if(cursor.moveToFirst()){
                lateOnlineTime = cursor.getLong(0);
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".queryLatestOnlineByEmpID->"+e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();

            if(null != cursor)
                cursor.close();
        }
        return lateOnlineTime;
    }

    /**
     * 根据shopID获取团队列表
     * @param shopID
     * @return
     */
    public List<ShopEmployeeVo> queryTeamByShopID(String shopID) {
        List<ShopEmployeeVo> shopEmployeeVos = new ArrayList<>();
        ShopEmployeeVo shopEmployeeVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.SHOP_EMPLOYEE_TBL, null, " shop_id = ? ", new String[]{ shopID }, null, null, " dept_id ASC");
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
