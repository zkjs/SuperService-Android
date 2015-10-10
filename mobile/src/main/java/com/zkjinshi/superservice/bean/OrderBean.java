package com.zkjinshi.superservice.bean;

import java.io.Serializable;

/**
 * 订单列表单条实体
 * 开发者：dujiande
 * 日期：2015/10/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderBean  implements Serializable {

    private String reservation_no;
    private String fullname;
    private String userid;
    private String shopid;
    private String arrival_date;
    private String departure_date;
    private String room_rate;
    private String room_type;
    private String room_typeid;
    private String guest;
    private String nologin;
    private int rooms;
    private String status;
    private String pay_status;
    private String created;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getReservation_no() {
        return reservation_no;
    }

    public void setReservation_no(String reservation_no) {
        this.reservation_no = reservation_no;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getArrival_date() {
        return arrival_date;
    }

    public void setArrival_date(String arrival_date) {
        this.arrival_date = arrival_date;
    }

    public String getDeparture_date() {
        return departure_date;
    }

    public void setDeparture_date(String departure_date) {
        this.departure_date = departure_date;
    }

    public String getRoom_rate() {
        return room_rate;
    }

    public void setRoom_rate(String room_rate) {
        this.room_rate = room_rate;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getRoom_typeid() {
        return room_typeid;
    }

    public void setRoom_typeid(String room_typeid) {
        this.room_typeid = room_typeid;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getNologin() {
        return nologin;
    }

    public void setNologin(String nologin) {
        this.nologin = nologin;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }


}
