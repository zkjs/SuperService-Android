package com.zkjinshi.superservice.pad.bean;

import java.io.Serializable;

/**
 * 订单列表单条实体
 * 开发者：dujiande
 * 日期：2015/10/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderBean implements Serializable {

    /**
     * 订单号
     */
    private String orderno;
    /**
     * 商店编号
     */
    private String shopid;
    /**
     * 商店名称
     */
    private String shopname;
    /**
     * 商店Logo
     */
    private String shoplogo;
    /**
     * 商品编号
     */
    private String productid;
    /**
     * 销售编号
     */
    private String saleid;
    /**
     * 用户编号
     */
    private String userid;
    /**
     * 订单状态(0:未确认 1:已确认 2:已取消)
     */
    private String orderstatus;
    /**
     * 房间编号
     */
    private String roomno;
    /**
     * 房间数量
     */
    private int roomcount;
    /**
     * 房间类型
     */
    private String roomtype;
    /**
     * 价格
     */
    private float roomprice;
    /**
     * 到店时间
     */
    private long arrivaldate;
    /**
     * 离店时间
     */
    private long leavedate;
    /**
     * 订单创建时间
     */
    private long created;
    /**
     * 联系电话
     */
    private String telephone;
    /**
     * 用户名
     */
    private String username;

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShoplogo() {
        return shoplogo;
    }

    public void setShoplogo(String shoplogo) {
        this.shoplogo = shoplogo;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getSaleid() {
        return saleid;
    }

    public void setSaleid(String saleid) {
        this.saleid = saleid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getRoomno() {
        return roomno;
    }

    public void setRoomno(String roomno) {
        this.roomno = roomno;
    }

    public int getRoomcount() {
        return roomcount;
    }

    public void setRoomcount(int roomcount) {
        this.roomcount = roomcount;
    }

    public String getRoomtype() {
        return roomtype;
    }

    public void setRoomtype(String roomtype) {
        this.roomtype = roomtype;
    }

    public float getRoomprice() {
        return roomprice;
    }

    public void setRoomprice(float roomprice) {
        this.roomprice = roomprice;
    }

    public long getArrivaldate() {
        return arrivaldate;
    }

    public void setArrivaldate(long arrivaldate) {
        this.arrivaldate = arrivaldate;
    }

    public long getLeavedate() {
        return leavedate;
    }

    public void setLeavedate(long leavedate) {
        this.leavedate = leavedate;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
