package com.zkjinshi.superservice.entity;

/**
 * 开发者：vincent
 * 日期：2015/10/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgSessionMemberInfo {

//    type MsgSessionMemberInfo struct {
//        UserID       string `json:"userid"`
//        OnlineStatus uint32 `json:"status"`              // 在线状态 0:在线 1:不在线
//        UserName     string `json:"username,omitempty"`  // 用户名
//        LoginType    uint32 `json:"logintype,omitempty"` // 用户类型.0:app用户  1:商家员工 默认为:0
//        ShopID       string `json:"shopid,omitempty"`    //
//    }

    private String userid;
    private int    status;    // 在线状态 0:在线 1:不在线
    private String username;  // 用户名
    private int    logintype;// 用户类型.0:app用户  1:商家员工 默认为:0
    private String shopid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLogintype() {
        return logintype;
    }

    public void setLogintype(int logintype) {
        this.logintype = logintype;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }
}
