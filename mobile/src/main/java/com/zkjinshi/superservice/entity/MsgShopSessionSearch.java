package com.zkjinshi.superservice.entity;

/**
 * 开发者：vincent
 * 日期：2015/10/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgShopSessionSearch {

//    //查找Session成员
//    type MsgShopSessionSearch struct {
//        MsgHeader
//        ShopID    string `json:"shopid"` //商家管理端ID
//        SessionID string `json:"sessionid"`
//    }

    private int     type;//not  null //协议消息类型
    private long    timestamp;//not  null //当前时间
    private int     protover;//消息协议版本

    private String  shopid;//
    private String  sessionid;//

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

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }
}
