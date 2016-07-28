package com.zkjinshi.superservice.vo;

import java.io.Serializable;

/**
 * 开发者：jimmyzhang
 * 日期：16/6/24
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceHistoryVo implements Serializable {

    /**
     "actioncode": 2,
     "actiondesc": "君子汇员工4 指派任务给 张仙华",
     "actionname": "指派",
     "assignId": "b_web576a5906d56e5",
     "assignName": "张仙华",
     "createtime": "2016-06-25 16:56:32",
     "userid": "b_web576a56e22d236",
     "userimage": "uploads/users/b_e6bf4b8e8c54f047_1461826666468.jpg",
     "username": "君子汇员工4"
     */

    private String username;
    private String userimage;
    private String actiondesc;
    private String createtime;
    private int actioncode;
    private String actionname;
    private String assignId;
    private String assignName;

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

    public String getActiondesc() {
        return actiondesc;
    }

    public void setActiondesc(String actiondesc) {
        this.actiondesc = actiondesc;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getActioncode() {
        return actioncode;
    }

    public void setActioncode(int actioncode) {
        this.actioncode = actioncode;
    }

    public String getActionname() {
        return actionname;
    }

    public void setActionname(String actionname) {
        this.actionname = actionname;
    }

    public String getAssignId() {
        return assignId;
    }

    public void setAssignId(String assignId) {
        this.assignId = assignId;
    }

    public String getAssignName() {
        return assignName;
    }

    public void setAssignName(String assignName) {
        this.assignName = assignName;
    }
}
