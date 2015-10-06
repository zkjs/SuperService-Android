package com.zkjinshi.superservice.bean;

/**
 * 获取商品列表 返回实体
 * 开发者：dujiande
 * 日期：2015/9/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodBean {

    private int id;
    private String room;
    private String type;
    private String imgurl;
    private String meat;
    private String price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getMeat() {
        return meat;
    }

    public void setMeat(String meat) {
        this.meat = meat;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
