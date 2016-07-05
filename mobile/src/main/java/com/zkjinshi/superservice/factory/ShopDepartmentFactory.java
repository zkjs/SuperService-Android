package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.database.Cursor;

import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.vo.DepartmentVo;
import com.zkjinshi.superservice.vo.OnlineStatus;

import com.zkjinshi.superservice.vo.WorkStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopDepartmentFactory {

    private static ShopDepartmentFactory instance = null;

    private ShopDepartmentFactory(){}

    public synchronized static ShopDepartmentFactory getInstance(){
        if(instance == null){
            instance = new ShopDepartmentFactory();
        }
        return instance;
    }

    /**
     *
     * @param departmentVo
     * @return
     */
    public ContentValues buildAddContentValues(DepartmentVo departmentVo) {

        ContentValues values = new ContentValues();
        values.put("deptid", departmentVo.getDeptid());
        values.put("shopid", departmentVo.getShopid());
//        values.put("dept_code", departmentVo.getDept_code());
//        values.put("dept_name", departmentVo.getDept_name());
//        values.put("description", departmentVo.getDescription());
        return values;
    }

    /**
     * 根据游标构建客户对象
     * @param cursor
     * @return
     */
    public DepartmentVo buildDepartmentVo(Cursor cursor) {
        DepartmentVo departmentVo = new DepartmentVo();
//        departmentVo.setDeptid(cursor.getInt(0));
//        departmentVo.setShopid(cursor.getString(1));
//        departmentVo.setDept_code(cursor.getString(2));
//        departmentVo.setDept_name(cursor.getString(3));
//        departmentVo.setDescription(cursor.getString(4));
        return  departmentVo;
    }

}
