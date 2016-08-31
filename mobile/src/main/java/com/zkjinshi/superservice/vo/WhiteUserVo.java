package com.zkjinshi.superservice.vo;

import java.io.Serializable;

/**
 * 开发者：JimmyZhang
 * 日期：2016/5/23
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class WhiteUserVo implements Serializable {

    /**
     "userid": "xx",
     "username": "xxx",
     "phone": "15815507111",
     "userimage": "uploads/users/b_56a5ced065fc3.jpg",
     "sex": 1,
     "email": null,
     "rmk":"xxx",
     "loginstatus": 1,  //0:未登录 1：已登录
     "createdby":"xxx",
     "lastvisit":"xxxx",  //null显示无记录
     */

    private String userid;
    private String username;
    private String userimage;
    private String phone;
    private int loginstatus;
    private String lastvisit;
    private String rmk;
    private int sex;

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

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getLoginstatus() {
        return loginstatus;
    }

    public void setLoginstatus(int loginstatus) {
        this.loginstatus = loginstatus;
    }

    public String getLastvisit() {
        return lastvisit;
    }

    public void setLastvisit(String lastvisit) {
        this.lastvisit = lastvisit;
    }

    public String getRmk() {
        return rmk;
    }

    public void setRmk(String rmk) {
        this.rmk = rmk;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
