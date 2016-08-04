package com.zkjinshi.superservice.pad.vo;

import java.io.Serializable;

/**
 * 开发者：jimmyzhang
 * 日期：16/6/25
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CallReadyVo implements Serializable {
    /**
     "alert": "王婆: 李二狗的人工按摩服务就绪",    //通知消息
     "createtime": "2016-07-01 06:00:00",  //就绪时间戳
     "taskid": "S9E44U",   //呼叫任务id
     "locid": "1001",    //区域id
     "locdesc": "大门",    //区域名称
     "srvname": "S9E44U",  //服务名称
     "userid": "c_ewiueo3u1",    //超级身份userid
     "username": "李二狗",   //超级身份username
     "usersex": 1,   //超级身份用户性别
     "userimage": "/uploads/head.png",   //用户头像
     "isowner": 1,   // 1:显示指派,0:显示取消
     "waiterid": "b_eaieuaow", //服务员userid
     "waitername": "王婆",   //服务员用户名
     "waiterimage": "/uploads/head.png", //服务员头像
     "operationseq": 2,    //操作序列号
     "statuscode": 3,  //任务状态码
     "status": "已就绪"
     */
    private String alert;
    private String createtime;
    private String taskid;
    private String locid;
    private String locdesc;
    private String srvname;
    private String userid;
    private String username;
    private int usersex;
    private String userimage;
    private int isowner;
    private String waiterid;
    private String waitername;
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

    public int getUsersex() {
        return usersex;
    }

    public void setUsersex(int usersex) {
        this.usersex = usersex;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public int getIsowner() {
        return isowner;
    }

    public void setIsowner(int isowner) {
        this.isowner = isowner;
    }

    public String getWaiterid() {
        return waiterid;
    }

    public void setWaiterid(String waiterid) {
        this.waiterid = waiterid;
    }

    public String getWaitername() {
        return waitername;
    }

    public void setWaitername(String waitername) {
        this.waitername = waitername;
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