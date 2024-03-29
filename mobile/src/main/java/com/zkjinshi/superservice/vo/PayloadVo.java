package com.zkjinshi.superservice.vo;

import java.io.Serializable;
import java.util.Set;

/**
 * 开发者：JimmyZhang
 * 日期：2016/2/26
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class PayloadVo implements Serializable {

    private String sub;//用户Id
    private int type;//Token的获得途径(1代表超级身份手机号, 2代表超级服务手机号, 3代表超级服务用户名),
    private long expire;//有效期
    private String shopid;//商家id
    private String[] roles;//角色编号
    private Set<String> features;//功能编号

    public Set<String> getFeatures() {
        return features;
    }

    public void setFeatures(Set<String> features) {
        this.features = features;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }
}
