package com.zkjinshi.superservice.entity;

/**
 * 开发者：vincent
 * 日期：2015/10/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UserOnlineStatusRecord {

//    type UserOnlineStatus_Record struct {
//        UserID string `json:"userid"`
//        UserName string `json:"username,omitempty"`
//        OnlineStatus uint32 `json:"onlinestatus"` // 0:在线 1:不在线
//        LoginTimestamp int64 `json:"logintimestamp,omitempty"` //上线的Unix Timestamp
//    }

    private String userid;
    private String username;
    private int    onlinestatus;
    private long   logintimestamp;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getLogintimestamp() {
        return logintimestamp;
    }

    public void setLogintimestamp(long logintimestamp) {
        this.logintimestamp = logintimestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getOnlinestatus() {
        return onlinestatus;
    }

    public void setOnlinestatus(int onlinestatus) {
        this.onlinestatus = onlinestatus;
    }
}

