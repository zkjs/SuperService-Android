package com.zkjinshi.superservice.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/10/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientBaseBean implements Serializable{

    private String userid;
    private String username;
    private int    sex;
    private String phone;
    private String position;
    private String company;
    private String card_no;
    private int    is_bill;
    private List<ClientTag> tags;
    private int    order_count;
    private String salesid;//绑定服务员ID
    private String salesname;//绑定服务员姓名
    private int    user_level;
    private String level_desc;

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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
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

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public int getIs_bill() {
        return is_bill;
    }

    public void setIs_bill(int is_bill) {
        this.is_bill = is_bill;
    }

    public List<ClientTag> getTags() {
        return tags;
    }

    public void setTags(List<ClientTag> tags) {
        this.tags = tags;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        this.order_count = order_count;
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

    public int getUser_level() {
        return user_level;
    }

    public void setUser_level(int user_level) {
        this.user_level = user_level;
    }

    public String getLevel_desc() {
        return level_desc;
    }

    public void setLevel_desc(String level_desc) {
        this.level_desc = level_desc;
    }
}
