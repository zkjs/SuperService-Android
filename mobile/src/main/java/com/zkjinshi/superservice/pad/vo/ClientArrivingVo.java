package com.zkjinshi.superservice.pad.vo;

import java.io.Serializable;

/**
 * 开发者：qinyejun
 * 日期：2016/7/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientArrivingVo implements Serializable {

    private String area;
    private String firstDate;
    private String firstFullDatetime;
    private String firstTime;
    private int locid;
    private int total;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public String getFirstFullDatetime() {
        return firstFullDatetime;
    }

    public void setFirstFullDatetime(String firstFullDatetime) { this.firstFullDatetime = firstFullDatetime; }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) { this.firstTime = firstTime; }

    public int getLocid() {
        return locid;
    }

    public void setLocid(int locid) {
        this.locid = locid;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) { this.total = total; }

}