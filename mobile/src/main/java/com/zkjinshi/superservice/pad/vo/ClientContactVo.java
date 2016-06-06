package com.zkjinshi.superservice.pad.vo;

import com.zkjinshi.superservice.pad.bean.BaseContact;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientContactVo extends BaseContact {

    private String userid;
    private String username;
    private String phone;
    private String password;
    private String realname;
    private String userimage;
    private int sex;
    private String email;
    private int userstatus;
    private int viplevel;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(int userstatus) {
        this.userstatus = userstatus;
    }

    public int getViplevel() {
        return viplevel;
    }

    public void setViplevel(int viplevel) {
        this.viplevel = viplevel;
    }
}
