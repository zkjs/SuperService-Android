package com.zkjinshi.superservice.bean;

/**
 * 服务员获取商家整个区域列表 返回实体
 * 开发者：dujiande
 * 日期：2015/9/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneBean {

//    "locid": 1,  int
//    "shopid": 120, int
//    "sensorid": 1, int
//    "uuid": "931ddf8e-10e4-11e5-9493-1697f925ec7b",
//    "major": 800, int
//    "minior": 1, int
//    "locdesc": "迎宾",
//    "status": 0, int  0有效,1失效
//    "remark": "备注信息"

    private int locid;
    private int shopid;
    private int sensorid;
    private String uuid;
    private int major;
    private int minior;
    private String locdesc;
    private int status;
    private String remark;
    private boolean hasAdd = false;

    public boolean isHasAdd() {
        return hasAdd;
    }

    public void setHasAdd(boolean hasAdd) {
        this.hasAdd = hasAdd;
    }

    public int getLocid() {
        return locid;
    }

    public void setLocid(int locid) {
        this.locid = locid;
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public int getSensorid() {
        return sensorid;
    }

    public void setSensorid(int sensorid) {
        this.sensorid = sensorid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinior() {
        return minior;
    }

    public void setMinior(int minior) {
        this.minior = minior;
    }

    public String getLocdesc() {
        return locdesc;
    }

    public void setLocdesc(String locdesc) {
        this.locdesc = locdesc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
