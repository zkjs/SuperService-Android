package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.database.Cursor;

import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;
import java.util.List;

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
     * @param shopEmployeeVo
     * @return
     */
    public ContentValues buildAddContentValues(ShopEmployeeVo shopEmployeeVo) {
        ContentValues values = new ContentValues();
        values.put("empid", shopEmployeeVo.getEmpid());
        values.put("empcode", shopEmployeeVo.getEmpcode());
        values.put("name", shopEmployeeVo.getName());
        values.put("roleid", shopEmployeeVo.getRoleid());
        values.put("email", shopEmployeeVo.getEmail());
        values.put("phone", shopEmployeeVo.getPhone());
        values.put("phone2", shopEmployeeVo.getPhone2());
        values.put("fax", shopEmployeeVo.getFax());
        values.put("created", shopEmployeeVo.getCreated());
        values.put("locationid", shopEmployeeVo.getLocationid());
        values.put("role_name", shopEmployeeVo.getRole_name());
        return values;
    }

    /**
     * 根据游标构建客户对象
     * @param cursor
     * @return
     */
    public ShopEmployeeVo buildShopEmployee(Cursor cursor) {
        ShopEmployeeVo shopEmployeeVo = new ShopEmployeeVo();
        shopEmployeeVo.setEmpid(cursor.getString(0));
        shopEmployeeVo.setEmpcode(cursor.getString(1));
        shopEmployeeVo.setName(cursor.getString(2));
        shopEmployeeVo.setRoleid(cursor.getInt(3));
        shopEmployeeVo.setEmail(cursor.getString(4));
        shopEmployeeVo.setPhone(cursor.getString(5));
        shopEmployeeVo.setPhone2(cursor.getString(6));
        shopEmployeeVo.setFax(cursor.getString(7));
        shopEmployeeVo.setCreated(cursor.getLong(8));
        shopEmployeeVo.setLocationid(cursor.getInt(9));
        shopEmployeeVo.setRole_name(cursor.getString(9));
        return  shopEmployeeVo;
    }

    /**
     *
     * @param teamContactBean
     * @return
     */
    public ShopEmployeeVo buildShopEmployee(TeamContactBean teamContactBean) {
        ShopEmployeeVo shopEmployeeVo = new ShopEmployeeVo();
        shopEmployeeVo.setEmpid(teamContactBean.getSalesid());
        shopEmployeeVo.setName(teamContactBean.getName());
        shopEmployeeVo.setPhone(teamContactBean.getPhone());
        shopEmployeeVo.setRoleid(teamContactBean.getRole_id());
        shopEmployeeVo.setRole_name(teamContactBean.getRole_name());
        return  shopEmployeeVo;
    }

    /**
     *
     * @param teamContactBeans
     * @return
     */
    public List<ShopEmployeeVo> buildShopEmployees(List<TeamContactBean> teamContactBeans) {
        List<ShopEmployeeVo> shopEmployeeVos = new ArrayList<>();
        ShopEmployeeVo       shopEmployeeVo  = null;
        for(TeamContactBean teamContactBean : teamContactBeans){
            shopEmployeeVo = buildShopEmployee(teamContactBean);
            shopEmployeeVos.add(shopEmployeeVo);
        }
        return shopEmployeeVos;
    }
}
