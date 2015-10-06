package com.zkjinshi.superservice.bean;


import java.io.Serializable;

/**
 * 获取订单详细 入住人信息实体
 * 开发者：dujiande
 * 日期：2015/10/06
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderUsersBean implements Serializable {
   private int id;//       联系人id int
   private String realname;//  真实姓名
   private String idcard;//   证件号
   private String phone;//    联系电话 phone

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
