package com.zkjinshi.superservice.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.ServiceApplication;
import com.zkjinshi.superservice.factory.ShopDepartmentFactory;
import com.zkjinshi.superservice.vo.DepartmentVo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopDepartmentDBUtil {

    private final static String TAG = ShopDepartmentDBUtil.class.getSimpleName();

    private Context mContext;
    private DBOpenHelper helper;

    private static ShopDepartmentDBUtil instance = null;

    private ShopDepartmentDBUtil(){
    }

    public synchronized static ShopDepartmentDBUtil getInstance(){
        if(instance == null){
            instance = new ShopDepartmentDBUtil();
        }
        instance.init();
        return instance;
    }

    private void init() {
        mContext = ServiceApplication.getContext();
        helper   = new DBOpenHelper(mContext);
    }

    /**
     * 根据部门查询部门
     * @param deptID
     * @return
     */
    public String queryDepartnameByDeptID(int deptID){

        String departName = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.SHOP_DEPARTMENT_TBL, null, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        DepartmentVo departmentVo = ShopDepartmentFactory.getInstance().buildDepartmentVo(cursor);
                        departName = departmentVo.getDept_name();
                        if(TextUtils.isEmpty(departName)){
                            return departName;
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.getInstance().info(LogLevel.ERROR, TAG+".queryDepartnameByDeptID->"+e.getMessage());
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
        return departName;
    }

    /**
     * 添加部门对象
     * @param departmentVo
     * @return
     */
    public long addShopDepartment(DepartmentVo departmentVo){
        ContentValues values = ShopDepartmentFactory.getInstance().buildAddContentValues(departmentVo);
        long addResult    = -1;
        SQLiteDatabase db = null;
        try {
            db        = helper.getWritableDatabase();
            addResult = db.insert(DBOpenHelper.SHOP_DEPARTMENT_TBL, null, values);
            if(addResult == -1){
                db.update(DBOpenHelper.SHOP_DEPARTMENT_TBL, values, " depid = ?", new String[] {departmentVo.getDeptid()+""});
            }
        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".addShopDepartment->" + e.getMessage());
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
        return addResult;
    }

    /**
     * @param departmentVos
     * @return
     */
    public long batchAddShopDepartments(List<DepartmentVo> departmentVos){
        long addResult    = -1;
        SQLiteDatabase db = null;
        try {
            db        = helper.getWritableDatabase();
            db.beginTransaction();
            for(DepartmentVo departmentVo : departmentVos){
                ContentValues values = ShopDepartmentFactory.getInstance().buildAddContentValues(departmentVo);
                try {
                    addResult = db.insert(DBOpenHelper.SHOP_DEPARTMENT_TBL, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(addResult == -1){
                    int deptID = departmentVo.getDeptid();
                    addResult = db.update(DBOpenHelper.SHOP_DEPARTMENT_TBL, values, " depid = ? ", new String[]{ deptID+"" });
                }
            }

        } catch (Exception e) {
            LogUtil.getInstance().info(LogLevel.ERROR, TAG + ".batchAddShopDepartments->" + e.getMessage());
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
    public List<DepartmentVo> queryAll() {
        List<DepartmentVo> departmentVos = new ArrayList<>();
        DepartmentVo departmentVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.SHOP_DEPARTMENT_TBL, null, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        departmentVo = ShopDepartmentFactory.getInstance().buildDepartmentVo(cursor);
                        departmentVos.add(departmentVo);
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
        return  departmentVos;
    }

}
