package com.zkjinshi.superservice.factory;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.zkjinshi.superservice.bean.SempLoginBean;
import com.zkjinshi.superservice.bean.UserBean;
import com.zkjinshi.superservice.vo.UserVo;

/**
 * 用户信息操作工厂类
 * 开发者：JimmyZhang
 * 日期：2015/9/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UserFactory {

    private UserFactory(){}

    private static UserFactory instance;

    public static synchronized UserFactory getInstance(){
        if(null == instance){
            instance = new UserFactory();
        }
        return instance;
    }

    public UserVo buildUserVo(SempLoginBean userBean){
        UserVo userVo = new UserVo();
        userVo.setUserId(userBean.getSalesid());
        userVo.setRoleId(userBean.getRoleid());
        userVo.setToken(userBean.getToken());
        userVo.setShopName(userBean.getFullname());
        userVo.setCellphone(userBean.getPhone());
        userVo.setPhotoUrl(userBean.getUrl());
        userVo.setShopId(userBean.getShopid());
        userVo.setUserName(userBean.getName());
        return userVo;
    }

    /**
     * 根据详细用户信息生成contentValues
     * @param userVo
     * @return
     */
    public ContentValues buildContentValues(UserVo userVo) {
        ContentValues values = new ContentValues();
        String userId = userVo.getUserId();
        String userName = userVo.getUserName();
        String shopId = userVo.getShopId();
        String cellphone = userVo.getCellphone();
        String photoUrl = userVo.getPhotoUrl();
        String shopName = userVo.getShopName();
        String token = userVo.getToken();
        String roleId = userVo.getRoleId();
        if(!TextUtils.isEmpty(userId)){
            values.put("user_id",userId);
        }
        if(!TextUtils.isEmpty(userName)){
            values.put("user_name",userName);
        }
        if(!TextUtils.isEmpty(shopId)){
            values.put("shop_id",shopId);
        }
        if(!TextUtils.isEmpty(cellphone)){
            values.put("cellphone",cellphone);
        }
        if(!TextUtils.isEmpty(photoUrl)){
            values.put("photo_url",photoUrl);
        }
        if(!TextUtils.isEmpty(shopName)){
            values.put("shop_name",shopName);
        }
        if(!TextUtils.isEmpty(token)){
            values.put("token",token);
        }
        if(!TextUtils.isEmpty(roleId)){
            values.put("role_id",roleId);
        }
        return values;
    }

    /**
     * 根据游标创建具体用户对象
     * @param cursor
     * @return
     */
    public UserVo buildUserVo(Cursor cursor) {
        UserVo userVo = new UserVo();
        userVo.setUserId(cursor.getString(0));
        userVo.setUserName(cursor.getString(1));
        userVo.setShopId(cursor.getString(2));
        userVo.setCellphone(cursor.getString(3));
        userVo.setPhotoUrl(cursor.getString(4));
        userVo.setShopName(cursor.getString(5));
        userVo.setToken(cursor.getString(6));
        userVo.setRoleId(cursor.getString(7));
        return userVo;
    }

}
