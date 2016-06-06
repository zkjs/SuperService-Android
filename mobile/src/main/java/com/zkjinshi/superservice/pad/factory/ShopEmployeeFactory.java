package com.zkjinshi.superservice.pad.factory;

import android.content.ContentValues;
import android.database.Cursor;

import com.zkjinshi.superservice.pad.vo.EmployeeVo;

/**
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopEmployeeFactory {

    private static ShopEmployeeFactory instance = null;

    private ShopEmployeeFactory(){}

    public synchronized static ShopEmployeeFactory getInstance(){
        if(instance == null){
            instance = new ShopEmployeeFactory();
        }
        return instance;
    }

    /**
     *
     * @param EmployeeVo
     * @return
     */
    public ContentValues buildContentValues(EmployeeVo EmployeeVo) {

        ContentValues values = new ContentValues();
        values.put("email", EmployeeVo.getEmail());
        values.put("phone", EmployeeVo.getPhone());
        values.put("realname", EmployeeVo.getRealname());
        values.put("rolecode", EmployeeVo.getRoleid());
        values.put("roledesc", EmployeeVo.getEmail());
        values.put("roleid", EmployeeVo.getPhone());
        values.put("rolename", EmployeeVo.getRolename());
        values.put("sex", EmployeeVo.getSex());
        values.put("userid", EmployeeVo.getUserid());
        values.put("userimage", EmployeeVo.getUserimage());
        values.put("username", EmployeeVo.getUsername());
        return values;
    }

    public EmployeeVo bulidEmployeeVo(Cursor cursor){
        String userid = cursor.getString(cursor.getColumnIndex("userid"));
        String email = cursor.getString(cursor.getColumnIndex("email"));
        String phone = cursor.getString(cursor.getColumnIndex("phone"));
        String realname = cursor.getString(cursor.getColumnIndex("realname"));
        String rolecode = cursor.getString(cursor.getColumnIndex("rolecode"));
        String roledesc = cursor.getString(cursor.getColumnIndex("roledesc"));
        String roleid = cursor.getString(cursor.getColumnIndex("roleid"));
        String rolename = cursor.getString(cursor.getColumnIndex("rolename"));
        int sex = cursor.getInt(cursor.getColumnIndex("sex"));
        String userimage = cursor.getString(cursor.getColumnIndex("userimage"));
        String username = cursor.getString(cursor.getColumnIndex("username"));

        EmployeeVo employeeVo = new EmployeeVo();
        employeeVo.setUserid(userid);
        employeeVo.setEmail(email);
        employeeVo.setPhone(phone);
        employeeVo.setRealname(realname);
        employeeVo.setRolecode(rolecode);
        employeeVo.setRoledesc(roledesc);
        employeeVo.setRoleid(roleid);
        employeeVo.setRolename(rolename);
        employeeVo.setSex(sex);
        employeeVo.setUserimage(userimage);
        employeeVo.setUsername(username);
        return  employeeVo;
    }
}
