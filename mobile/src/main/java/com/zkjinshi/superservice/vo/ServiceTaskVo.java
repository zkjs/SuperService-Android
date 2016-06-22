package com.zkjinshi.superservice.vo;

import java.io.Serializable;

/**
 * 开发者：jimmyzhang
 * 日期：16/6/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceTaskVo implements Serializable {

    private UserVo user;
    private String place;//服务场所
    private String project;//服务项目
    private String time;
    private String status;
    private String taskid;
    private int flow;//1. 发起任务 2. 指派 3. 就绪 4.取消 5 完成 6 评价
    private int isowner;//0:是所有者用来区分指派和取消按钮

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public UserVo getUser() {
        return user;
    }

    public void setUser(UserVo user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public int getIsowner() {
        return isowner;
    }

    public void setIsowner(int isowner) {
        this.isowner = isowner;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }
}
