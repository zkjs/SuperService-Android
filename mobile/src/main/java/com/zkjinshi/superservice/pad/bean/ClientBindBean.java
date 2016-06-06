package com.zkjinshi.superservice.pad.bean;

import java.io.Serializable;

/**
 * 用户是否使用邀请码并在指定商家存在专属客服
 * 开发者：vincent
 * 日期：2015/10/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientBindBean implements Serializable{

    private String userid;
    private String username;
    private String sex;
    private String phone;
    private String position;
    private String company;
    private boolean set;
    private boolean is_bd;
    private String salesid;
    private String salesname;

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public boolean isSet() {
        return set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    public boolean is_bd() {
        return is_bd;
    }

    public void setIs_bd(boolean is_bd) {
        this.is_bd = is_bd;
    }

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    public String getSalesname() {
        return salesname;
    }

    public void setSalesname(String salesname) {
        this.salesname = salesname;
    }
}
