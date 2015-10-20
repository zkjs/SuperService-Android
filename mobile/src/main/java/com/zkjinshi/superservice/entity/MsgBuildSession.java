package com.zkjinshi.superservice.entity;

import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/10/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgBuildSession {

//    MsgHeader
//    SessionID   string              `json:"sessionid"`
//    SessionName string              `json:"sessionname,omitempty"` //商家管理端ID
//    ShopID      string              `json:"shopid,omitempty"`      //商家管理端ID
//    UserID      string              `json:"userid,omitempty"`      // 创建者
//    Detail      []*MsgIMSessionUser `json:"detail"`                //会话成员

    private int     type;//not  null //协议消息类型
    private long    timestamp;//not  null //当前时间
    private int     protover;//消息协议版本

    private String  sessionid;//
    private String  sessionname;//会话名称
    private String  shopid;//商家管理端ID
    private String  userid;//创建者
    private List<MsgIMSessionUser> detail;//会话成员

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

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getSessionname() {
        return sessionname;
    }

    public void setSessionname(String sessionname) {
        this.sessionname = sessionname;
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

    public List<MsgIMSessionUser> getDetail() {
        return detail;
    }

    public void setDetail(List<MsgIMSessionUser> detail) {
        this.detail = detail;
    }
}
