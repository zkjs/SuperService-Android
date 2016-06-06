package com.zkjinshi.superservice.pad.ext.vo;

import java.io.Serializable;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class NearbyUserVo implements Serializable {
    /**
     "locid": "区域编号",
     "userid": "用户编号",
     "username": "用户名称",
     "userlevel": "用户级别",
     "usericon": "/uploads/zkjs.jpg",
     "sex": "用户性别",
     "phone": "用户电话",
     "city": "用户所在城市",
     "shopid": "商家编号",
     "shopname": "商家名称",
     "arrivetime": "到达所在区域的时间",
     "orders": [
     {
     "orderno": "订单号",
     "room": "房间类型",
     "duration": "入住时长",
     "indate": "入住时间"
     }
     ]
     */
    private String locid;
    private String userid;
    private String username;
    private String userlevel;
    private String userimage;
    private String sex;
    private String phone;
    private String city;
    private String shopid;
    private String shopname;
    private String arrivetime;

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserlevel() {
        return userlevel;
    }

    public void setUserlevel(String userlevel) {
        this.userlevel = userlevel;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getArrivetime() {
        return arrivetime;
    }

    public void setArrivetime(String arrivetime) {
        this.arrivetime = arrivetime;
    }
}
