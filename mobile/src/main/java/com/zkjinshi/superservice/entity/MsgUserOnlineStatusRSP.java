package com.zkjinshi.superservice.entity;

import java.util.List;

/**
 * 开发者：vincent
 * 日期：2015/10/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgUserOnlineStatusRSP {

//    type MsgUserOnlineStatus_RSP struct {
//        MsgHeader
//        Result []*UserOnlineStatus_Record `json:"result,omitempty"`
//    }

    private int  type;//not  null //协议消息类型
    private long timestamp;//not  null //当前时间
    private int  protover;//消息协议版本

    private List<UserOnlineStatusRecord> result;

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

    public List<UserOnlineStatusRecord> getResult() {
        return result;
    }

    public void setResult(List<UserOnlineStatusRecord> result) {
        this.result = result;
    }
}
