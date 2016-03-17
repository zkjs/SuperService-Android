package com.zkjinshi.superservice.bean;

/**
 * 服务员获取商家整个区域列表 返回实体
 * 开发者：dujiande
 * 日期：2015/9/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneBean {

/*           "locdesc" "演示区",    //区域描述
            "locid": "1",
            "major": "2",
            "minor": "3",
            "uuid": "uuid-uuid-uuid-uuid",
            "sensorid": "sensorid",
            "subscribed": 1 //是否已订阅: 1-已订阅, 0-未订阅*/

    private String locdesc;
    private String locid;
    private String major;
    private String minor;
    private String uuid;
    private String sensorid;
    private int subscribed;

    public String getLocdesc() {
        return locdesc;
    }

    public void setLocdesc(String locdesc) {
        this.locdesc = locdesc;
    }

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    public int getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(int subscribed) {
        this.subscribed = subscribed;
    }
}
