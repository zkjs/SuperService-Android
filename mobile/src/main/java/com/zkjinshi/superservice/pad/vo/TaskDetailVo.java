package com.zkjinshi.superservice.pad.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 任务追踪明细
 * 开发者：jimmyzhang
 * 日期：16/6/24
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class TaskDetailVo implements Serializable{

    private String taskid;
    private String userid;
    private String username;
    private String userimage;
    private String locdesc;
    private String srvname;
    private String createtime;
    private ArrayList<ServiceHistoryVo> history;

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
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

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
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

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public ArrayList<ServiceHistoryVo> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<ServiceHistoryVo> history) {
        this.history = history;
    }
}