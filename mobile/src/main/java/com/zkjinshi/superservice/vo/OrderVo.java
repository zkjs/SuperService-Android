package com.zkjinshi.superservice.vo;

/**
 * 订单信息
 * 开发者：JimmyZhang
 * 日期：2016/1/6
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderVo {

    /**
     {
     "orderno": "订单号",
     "room": "房间类型",
     "duration": "入住时长",
     "indate": "入住时间"
     }
     */

    private String orderno;
    private String indate;
    private String duration;
    private String room;

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getIndate() {
        return indate;
    }

    public void setIndate(String indate) {
        this.indate = indate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
