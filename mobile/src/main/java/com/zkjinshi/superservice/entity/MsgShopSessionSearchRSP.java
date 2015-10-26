package com.zkjinshi.superservice.entity;

import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/10/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgShopSessionSearchRSP {

//    //列出Session成员
//    type MsgShopSessionSearch_RSP struct {
//        MsgHeader
//        ShopID    string                  `json:"shopid"`    //商家管理端ID
//        SessionID string                  `json:"sessionid"` //会话ID
//        Detail    []*MsgSessionMemberInfo `json:"detail"`    //成员
//        Result    uint32                  `json:"result"`    // 0:成功 1:失败
//    }

    private int     type;//not  null //协议消息类型
    private long    timestamp;//not  null //当前时间
    private int     protover;//消息协议版本

    private String  shopid;//商家管理端ID
    private String  sessionid;//会话ID
    private int     result;//0:成功 1:失败
    private List<MsgSessionMemberInfo> detail;//成员

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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<MsgSessionMemberInfo> getDetail() {
        return detail;
    }

    public void setDetail(List<MsgSessionMemberInfo> detail) {
        this.detail = detail;
    }
}
