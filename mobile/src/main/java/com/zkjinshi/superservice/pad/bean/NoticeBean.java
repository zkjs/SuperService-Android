package com.zkjinshi.superservice.pad.bean;

/**
 * 开发者：dujiande
 * 日期：2015/10/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NoticeBean extends BaseBean {

    private String userid;
    private String username;
    private String sex;
    private String phone;
    private String qq;
    private String birthday;
    private String user_level;
    private BookOrderBean order;
    private int order_count;

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

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public BookOrderBean getOrder() {
        return order;
    }

    public void setOrder(BookOrderBean order) {
        this.order = order;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        this.order_count = order_count;
    }

    public String getUser_level() {
        return user_level;
    }

    public void setUser_level(String user_level) {
        this.user_level = user_level;
    }
}
