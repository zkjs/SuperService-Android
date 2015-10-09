package com.zkjinshi.superservice.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 会员详细个人信息实体类
 * 开发者：vincent
 * 日期：2015/10/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientDetailBean implements Serializable{

    private String    userid;
    private String    username;
    private int       sex;
    private String    phone;
    private String    position;
    private String    company;
    private String    card_no;
    private int       is_bill;
    private int       order_count;

    private List<ClientTag>     tags;

    public class ClientTag{
        public int    tagid;
        public String tag;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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
}
