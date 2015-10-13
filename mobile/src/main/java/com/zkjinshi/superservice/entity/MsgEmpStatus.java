package com.zkjinshi.superservice.entity;

import org.json.JSONArray;

/**
 * 开发者：vincent
 * 日期：2015/10/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgEmpStatus {

//      //当emps为空，则返回商家所有员工状态列表,否则只返回
//      // emp数组中指定员工id的状态
//    type MsgEmpStatus struct {
//        MsgHeader
//        ShopID string   `json:"shopid"`
//        Emp    []string `json:"emps,omitempty"` //员工ID字符数组
//    }
    private int     type;//not  null //协议消息类型
    private long    timestamp;//not  null //当前时间
    private String  tempid;//app端临时ID
    private long    srvmsgid;
    private int     protover;//消息协议版本

    private String    shopid;
    private JSONArray emps;  //员工ID数组

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTempid() {
        return tempid;
    }

    public void setTempid(String tempid) {
        this.tempid = tempid;
    }

    public long getSrvmsgid() {
        return srvmsgid;
    }

    public void setSrvmsgid(long srvmsgid) {
        this.srvmsgid = srvmsgid;
    }

    public int getProtover() {
        return protover;
    }

    public void setProtover(int protover) {
        this.protover = protover;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public JSONArray getEmps() {
        return emps;
    }

    public void setEmps(JSONArray emps) {
        this.emps = emps;
    }
}
