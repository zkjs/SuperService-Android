package com.zkjinshi.superservice.entity;

/**
 * 开发者：vincent
 * 日期：2015/10/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgBuildSessionRSP {

//    type MsgBuildSession_RSP struct {
//        MsgHeader
//        SessionID string `json:"sessionid"`
//        Result    uint32 `json:"result"` // 0:成功 1:失败
//    }

    private int     type;//not  null //协议消息类型
    private long    timestamp;//not  null //当前时间
    private int     protover;//消息协议版本

    private String sessionid;
    private int    result; // 0:成功 1:失败

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProtover() {
        return protover;
    }

    public void setProtover(int protover) {
        this.protover = protover;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
}
