package com.zkjinshi.superservice.vo;

import java.io.Serializable;

/**
 * 开发者：jimmyzhang
 * 日期：16/6/30
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class EventUserVo implements Serializable {

    /**
     "userid": "",

     "username": "",

     "takeperson": xx,

     "confirmstatus": "",

     "confirmstatusCode":xx,
     */

    private String userid;
    private String username;
    private int takeperson;
    private String confirmstatus;
    private int confirmstatusCode;

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

    public int getTakeperson() {
        return takeperson;
    }

    public void setTakeperson(int takeperson) {
        this.takeperson = takeperson;
    }

    public String getConfirmstatus() {
        return confirmstatus;
    }

    public void setConfirmstatus(String confirmstatus) {
        this.confirmstatus = confirmstatus;
    }

    public int getConfirmstatusCode() {
        return confirmstatusCode;
    }

    public void setConfirmstatusCode(int confirmstatusCode) {
        this.confirmstatusCode = confirmstatusCode;
    }
}
