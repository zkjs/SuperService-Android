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
    private String username;
    private String created;
    private String userid;

    public String getSalecode() {
        return salecode;
    }

    public void setSalecode(String salecode) {
        this.salecode = salecode;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
