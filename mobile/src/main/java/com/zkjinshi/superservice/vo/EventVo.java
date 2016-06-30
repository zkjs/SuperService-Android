package com.zkjinshi.superservice.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 活动实体
 * 开发者：jimmyzhang
 * 日期：16/6/28
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class EventVo implements Serializable {

    /**
     "actname": "",

     "actid": xx,

     "startdate":"",

     "enddate":"",

     "invitedpersoncnt": 10,

     "confirmpersoncnt": 2,

     "inviteperson": [{

     "userid": "",

     "username": "",

     "takeperson": xx,

     "confirmstatus": "",

     "confirmstatusCode":xx,

     }]

     }]
     */

    private String actname;
    private String actid;
    private String startdate;
    private String enddate;
    private int invitedpersoncnt;
    private int confirmpersoncnt;

    private ArrayList<EventUserVo> inviteperson;

    public String getActname() {
        return actname;
    }

    public void setActname(String actname) {
        this.actname = actname;
    }

    public String getActid() {
        return actid;
    }

    public void setActid(String actid) {
        this.actid = actid;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public int getInvitedpersoncnt() {
        return invitedpersoncnt;
    }

    public void setInvitedpersoncnt(int invitedpersoncnt) {
        this.invitedpersoncnt = invitedpersoncnt;
    }

    public int getConfirmpersoncnt() {
        return confirmpersoncnt;
    }

    public void setConfirmpersoncnt(int confirmpersoncnt) {
        this.confirmpersoncnt = confirmpersoncnt;
    }

    public ArrayList<EventUserVo> getInviteperson() {
        return inviteperson;
    }

    public void setInviteperson(ArrayList<EventUserVo> inviteperson) {
        this.inviteperson = inviteperson;
    }
}