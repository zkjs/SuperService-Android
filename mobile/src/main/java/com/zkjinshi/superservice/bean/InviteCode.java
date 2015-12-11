package com.zkjinshi.superservice.bean;

import java.io.Serializable;

/**
 * 邀请码对象
 * 开发者：WinkyQin
 * 日期：2015/11/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCode implements Serializable{

    private String salesid;
    private int    codeid;
    private String salecode;//邀请码
    private int    is_validity; //0有效,1失效(已被使用)

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    public int getCodeid() {
        return codeid;
    }

    public void setCodeid(int codeid) {
        this.codeid = codeid;
    }

    public String getSalecode() {
        return salecode;
    }

    public void setSalecode(String salecode) {
        this.salecode = salecode;
    }

    public int getIs_validity() {
        return is_validity;
    }

    public void setIs_validity(int is_validity) {
        this.is_validity = is_validity;
    }
}
