package com.zkjinshi.superservice.entity;

import java.io.Serializable;

/**
 * 到店通知消息对象
 * 开发者：vincent
 * 日期：2015/9/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgPushTriggerLocNotificationM2S implements Serializable{

    private int     type;//not  null //协议消息类型
    private long    timestamp;//not  null //当前时间
    private String  tempid;//app端临时ID
    private long    srvmsgid;
    private int     protover;//消息协议版本

    private String  shopid;//商店id
    private String  userid;//客人的userid
    private String  username;//username
    private String  locid;   //客人所在区域
    private String locdesc;//客人区域描述

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getSrvmsgid() {
        return srvmsgid;
    }

    public void setSrvmsgid(long srvmsgid) {
        this.srvmsgid = srvmsgid;
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

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }

    public String getLocdesc() {
        return locdesc;
    }

    public void setLocdesc(String locdesc) {
        this.locdesc = locdesc;
    }
}
