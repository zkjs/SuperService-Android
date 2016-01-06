package com.zkjinshi.superservice.vo;

import java.util.ArrayList;

/**
 * 当店通知
 * 开发者：JimmyZhang
 * 日期：2016/1/6
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class NoticeVo {

    private String created;
    private String locId;
    private ArrayList<OrderVo> orderForNotice;
    private String phone;
    private String sex;
    private String shopId;
    private String shopName;
    private int userApplevel;
    private String userId;
    private String userName;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLocId() {
        return locId;
    }

    public void setLocId(String locId) {
        this.locId = locId;
    }

    public ArrayList<OrderVo> getOrderForNotice() {
        return orderForNotice;
    }

    public void setOrderForNotice(ArrayList<OrderVo> orderForNotice) {
        this.orderForNotice = orderForNotice;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getUserApplevel() {
        return userApplevel;
    }

    public void setUserApplevel(int userApplevel) {
        this.userApplevel = userApplevel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
