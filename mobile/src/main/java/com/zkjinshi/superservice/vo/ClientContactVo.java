package com.zkjinshi.superservice.vo;

import com.zkjinshi.superservice.bean.BaseContact;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientContactVo extends BaseContact{

    private String id;
    private String userid;
    private String phone;
    private String fuid;
    private String fname;
    private String shopid;
    private String created;
    private String shop_name;
    private String teamid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFuid() {
        return fuid;
    }

    public void setFuid(String fuid) {
        this.fuid = fuid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getTeamid() {
        return teamid;
    }

    public void setTeamid(String teamid) {
        this.teamid = teamid;
    }
}
