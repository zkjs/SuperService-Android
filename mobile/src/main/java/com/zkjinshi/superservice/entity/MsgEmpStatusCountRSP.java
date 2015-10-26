package com.zkjinshi.superservice.entity;

/**
 * 开发者：JimmyZhang
 * 日期：2015/10/14
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgEmpStatusCountRSP {

    private int type;//协议消息类型
    private long timestamp;//当前时间
    private String tempid;//app端临时ID
    private long srvmsgid;
    private int protover;//消息协议版本
    private String shopid;
    private int onlinecount;
    private int workcount;

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

    public int getOnlinecount() {
        return onlinecount;
    }

    public void setOnlinecount(int onlinecount) {
        this.onlinecount = onlinecount;
    }

    public int getWorkcount() {
        return workcount;
    }

    public void setWorkcount(int workcount) {
        this.workcount = workcount;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }
}
