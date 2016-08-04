package com.zkjinshi.superservice.pad.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 开发者：jimmyzhang
 * 日期：16/6/28
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class GuestVo implements Serializable {

    private String roleid;
    private String rolename;
    private ArrayList<MemberVo> member;
    private int count;

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public ArrayList<MemberVo> getMember() {
        return member;
    }

    public void setMember(ArrayList<MemberVo> member) {
        this.member = member;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
