package com.zkjinshi.superservice.bean;

/**
 * 查询邀请码记录对象
 * 开发者：WinkyQin
 * 日期：2015/11/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeUser {

    private String salecode;
    private String userid;
    private String username;
    private String created;
    private String phone;
    private String lasttime;
    private int    user_applevel;

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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public int getUser_applevel() {
        return user_applevel;
    }

    public void setUser_applevel(int user_applevel) {
        this.user_applevel = user_applevel;
    }
}
