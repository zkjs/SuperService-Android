package com.zkjinshi.superservice.vo;

/**
 * 订单信息
 * 开发者：JimmyZhang
 * 日期：2016/1/6
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderVo {

    private String orderNo;
    private String checkIn;
    private String checkInDate;
    private String orderRoom;

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getOrderRoom() {
        return orderRoom;
    }

    public void setOrderRoom(String orderRoom) {
        this.orderRoom = orderRoom;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
