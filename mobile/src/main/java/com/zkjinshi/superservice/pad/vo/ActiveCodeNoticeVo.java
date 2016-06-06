package com.zkjinshi.superservice.pad.vo;

import java.io.Serializable;

/**
 * 激活邀请码通知实体
 * 开发者：JimmyZhang
 * 日期：2016/3/14
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ActiveCodeNoticeVo implements Serializable {

    private long create;
    private String phone;
    private String salecode;
    private String userid;
    private String userimage;
    private String username;

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public long getCreate() {
        return create;
    }

    public void setCreate(long create) {
        this.create = create;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSalecode() {
        return salecode;
    }

    public void setSalecode(String salecode) {
        this.salecode = salecode;
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
}
