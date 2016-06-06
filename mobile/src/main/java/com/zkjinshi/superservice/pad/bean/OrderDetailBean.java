package com.zkjinshi.superservice.pad.bean;


import java.io.Serializable;

/**
 * 获取订单详细 返回实体
 * 开发者：dujiande
 * 日期：2015/10/06
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderDetailBean implements Serializable {

    /**
     * 订单号
     */
    private String orderno;
    /**
     * 商店编号
     */
    private String shopid;
    /**
     * 用户id
     */
    private String userid;
    /**
     * 销售id
     */
    private String saleid;

    /**
     * 商店名称
     */
    private String shopname;
    /**
     * 商品图片
     */
    private String imgurl;
    /**
     * 商品编号
     */
    private String productid;
    /**
     * 房号
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
     * 支付方式
     */
    private int paytype;
    /**
     * 价格
     */
    private float roomprice;
    /**
     * 预订人
     */
    private String orderedby;
    /**
     * 联系电话
     */
    private String telephone;
    /**
     * 到店时间
     */
    private String arrivaldate;
    /**
     * 离店时间
     */
    private String leavedate;
    /**
     * 人数
     */
    private int personcount;
    /**
     * 双份早餐
     */
    private int doublebreakfeast;
    /**
     * 无烟房
     */
    private int nosmoking;
    /**
     * 公司名称(用于发票信息)
     */
    private String company;
    /**
     * 是否需要发票
     */
    private int isinvoice;
    /**
     * 备注
     */
    private String remark;
    /**
     *用户名
     */
    private String username;
    /**
     * 订单状态
     */
    private String orderstatus;

    /**
     * 订单创建时间
     */
    private long created;

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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSaleid() {
        return saleid;
    }

    public void setSaleid(String saleid) {
        this.saleid = saleid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
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

    public int getPaytype() {
        return paytype;
    }

    public void setPaytype(int paytype) {
        this.paytype = paytype;
    }

    public float getRoomprice() {
        return roomprice;
    }

    public void setRoomprice(float roomprice) {
        this.roomprice = roomprice;
    }

    public String getOrderedby() {
        return orderedby;
    }

    public void setOrderedby(String orderedby) {
        this.orderedby = orderedby;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getArrivaldate() {
        return arrivaldate;
    }

    public void setArrivaldate(String arrivaldate) {
        this.arrivaldate = arrivaldate;
    }

    public String getLeavedate() {
        return leavedate;
    }

    public void setLeavedate(String leavedate) {
        this.leavedate = leavedate;
    }

    public int getPersoncount() {
        return personcount;
    }

    public void setPersoncount(int personcount) {
        this.personcount = personcount;
    }

    public int getDoublebreakfeast() {
        return doublebreakfeast;
    }

    public void setDoublebreakfeast(int doublebreakfeast) {
        this.doublebreakfeast = doublebreakfeast;
    }

    public int getNosmoking() {
        return nosmoking;
    }

    public void setNosmoking(int nosmoking) {
        this.nosmoking = nosmoking;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getIsinvoice() {
        return isinvoice;
    }

    public void setIsinvoice(int isinvoice) {
        this.isinvoice = isinvoice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
