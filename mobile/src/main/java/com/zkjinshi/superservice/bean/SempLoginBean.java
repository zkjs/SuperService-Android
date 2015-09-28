package com.zkjinshi.superservice.bean;

/**
 * 开发者：dujiande
 * 日期：2015/9/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SempLoginBean extends BaseBean {
//    "set": true,
//    "salesid": "5577ecee5acc7",  服务员id,tokenid
//    "shopid": 120, 商家id
//    "phone": 19912345678, 手机号
//    "url":"uploads/user/55.....j", 没有头像时 null 是不是首次登陆的判断依据.
//    "fullname": "长沙芙蓉国温德姆至尊豪廷大酒店",  商家全称
//    "token": "kRjaw5EW7WeBHcha",  token
//    "name": "李大钊",
//    "roleid": 4  角色id

    private String salesid;
    private String shopid;
    private String phone;
    private String url;
    private String fullname;
    private String token;
    private String name;
    private String roleid;

    public String getSalesid() {
        return salesid;
    }

    public void setSalesid(String salesid) {
        this.salesid = salesid;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }


}
