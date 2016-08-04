package com.zkjinshi.superservice.pad.vo;

import com.blueware.agent.android.harvest.P;

import java.io.Serializable;

/**
 * 开发者：jimmyzhang
 * 日期：16/6/25
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CallAssignVo implements Serializable {

    /**
     "alert": "请为李二狗准备人工按摩服务",   //通知消息
     "createtime": "2016-07-01 06:00:00",  //指派时间
     "taskid": "S9E44U",   //指派任务的id
     "locid": "1001",    //区域id
     "locdesc": "大门",    //区域名称
     "srvname": "人工按摩",    //服务名称
     "username": "李二狗",   //超级身份username
     "userimage": "/uploads/head.png",   //用户头像
     "usersex": 1,   //超级身份用户性别
     "userid": "c_56a5ced065fc3",    //超级身份userid
     "isowner": 0,   // 1:显示指派,0:显示取消
     "operationseq": 1,    //操作序列号
     "statuscode": 2,  //任务状态码
     "status": "已指派"
     */

    private String alert;
    private String createtime;
    private String taskid;
    private String locid;
    private String locdesc;
    private String srvname;
    private String username;
    private String userimage;
    private int usersex;
    private String userid;
    private int isowner;
    private int operationseq;
    private int statuscode;
    private String status;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
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

    public String getSrvname() {
        return srvname;
    }

    public void setSrvname(String srvname) {
        this.srvname = srvname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public int getUsersex() {
        return usersex;
    }

    public void setUsersex(int usersex) {
        this.usersex = usersex;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getIsowner() {
        return isowner;
    }

    public void setIsowner(int isowner) {
        this.isowner = isowner;
    }

    public int getOperationseq() {
        return operationseq;
    }

    public void setOperationseq(int operationseq) {
        this.operationseq = operationseq;
    }

    public int getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}