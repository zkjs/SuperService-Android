package com.zkjinshi.superservice.entity;

/**
 * 开发者：vincent
 * 日期：2015/10/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgIMSessionUser {

//    type MsgIMSessionUser struct {
//        UserID   string `json:"userid"`
//        ShopID   string `json:"shopid,omitempty"`
//        UserName string `json:"username,omitempty"` //创建时带上用户名
//        IsAdmin  uint32 `json:"isadmin,omitempty"`  //0:普通 1:管理员
//    }

    private String shopid;
    private String userid;
    private String username;//创建时带上用户名
    private int    isadmin;//0:普通 1:管理员

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public int getIsadmin() {
        return isadmin;
    }

    public void setIsadmin(int isadmin) {
        this.isadmin = isadmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
