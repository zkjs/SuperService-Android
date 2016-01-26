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

//        {"userid":"555feb81654ae",
//        "username":"winky",
//        "sex":0,
//        "phone":18695691235,
//        "position":"\u592a\u9633\u795e",
//        "company":"",
//        "fuid":"5639a18dc1905",
//        "tags":[],
//        "order_count":0,
//        "salesname":"\u6881\u7ba1\u7406\u5458\u5185\u540d\u79f0"}
//        "card_no": 'A00087', 会员卡号
//        "is_bill": 1, 是否挂账 1是,0不是
//        "salesid": "绑定的服务员id"

    private String userid;
    private String username;
    private int    sex;
    private String phone;
    private String position;
    private String company;
    private List<ClientTag> tags;
    private String fuid;//绑定服务员ID
    private String salesname;//绑定服务员姓名
    private int    order_count;
    private int    is_bill;
    private String card_no;
    private String salesid;//绑定服务员ID

    private boolean set;
    private int     err;

//    private int    user_level;
//    private String level_desc;

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

    public List<ClientTag> getTags() {
        return tags;
    }

    public void setTags(List<ClientTag> tags) {
        this.tags = tags;
    }

    public String getFuid() {
        return fuid;
    }

    public void setFuid(String fuid) {
        this.fuid = fuid;
    }

    public String getSalesname() {
        return salesname;
    }

    public void setSalesname(String salesname) {
        this.salesname = salesname;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        this.order_count = order_count;
    }

    public int getIs_bill() {
        return is_bill;
    }

    public void setIs_bill(int is_bill) {
        this.is_bill = is_bill;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    public boolean isSet() {
        return set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }
}
