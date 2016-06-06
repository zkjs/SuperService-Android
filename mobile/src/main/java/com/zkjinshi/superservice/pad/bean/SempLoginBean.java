package com.zkjinshi.superservice.pad.bean;

/**
 * 登录api返回实体
 * 开发者：dujiande
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SempLoginBean extends BaseBean {

    /**
     "set": true,
     "locid": "1,2,3,6,8,11,14,16,26",
     "salesid": "55d67f785e6cb",
     "shopid": 120,
     "phone": 15919805819,
     "url": "uploads/users/55d67f785e6cb.jpg",
     "fullname": "长沙豪廷大酒店",
     "token": "5XZLU4-3fhk1c9TF",
     "name": "张仙华",
     "roleid": 2
     */

    private String locid;
    private String salesid;
    private String shopid;
    private String phone;
    private String url;
    private String fullname;
    private String token;
    private String name;
    private String roleid;
    private int sex=0; //0 是女 1 是男

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
