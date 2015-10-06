package com.zkjinshi.superservice.bean;



import java.io.Serializable;

/**
 * 获取订单详细 服务特权实体
 * 开发者：dujiande
 * 日期：2015/10/06
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderPrivilegeBean implements Serializable {
    private int id;//                   商家特权id  int
    private String privilege_code;//   特权代码
    private String privilege_name;//   特权名称
    private String user_level;//       使用此特权要求最低用户级别  int

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrivilege_code() {
        return privilege_code;
    }

    public void setPrivilege_code(String privilege_code) {
        this.privilege_code = privilege_code;
    }

    public String getPrivilege_name() {
        return privilege_name;
    }

    public void setPrivilege_name(String privilege_name) {
        this.privilege_name = privilege_name;
    }

    public String getUser_level() {
        return user_level;
    }

    public void setUser_level(String user_level) {
        this.user_level = user_level;
    }
}
