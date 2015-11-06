package com.zkjinshi.superservice.entity;

/**
 * 开发者：WinkyQin
 * 日期：2015/11/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgOfflineMessage {

//    //得到离线消息
//    type MsgOfflineMessage struct {
//        MsgHeader
//        UserID string `json:"userid"`
//    }

    public int     type;//not  null //协议消息类型
    public long    timestamp;//not  null //当前时间
    public int     protover;//消息协议版本
    public String   userid;//用户ID

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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
