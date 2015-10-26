package com.zkjinshi.superservice.bean;

/**
 * 登录api返回实体
 * 开发者：dujiande
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class AdminLoginBean extends BaseBean {

    /**
     * "set": true,
     "userid": "5577ecee5acc7",
     "shopid": "120",
     "fullname": "长沙芙蓉国温德姆至尊豪廷大酒店",
     "token": "jxT3sB_U2M2EsGF_",
     "name": "王二麻子",
     "roleid": "4",
     "locid": "1,2,3"
     */

    private String userid;
    private String shopid;
    private String fullname;
    private String phone;
    private String token;
    private String name;
    private String roleid;
    private String locid;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }
}
