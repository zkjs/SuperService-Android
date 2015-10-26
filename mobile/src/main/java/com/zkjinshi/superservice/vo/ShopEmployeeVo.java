package com.zkjinshi.superservice.vo;

import java.io.Serializable;

/**
 *
 * 开发者：vincent
 * 日期：2015/10/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopEmployeeVo implements Serializable{

    private String empid;
    private String empcode;
    private String name;
    private int    roleid;
    private String email;
    private String phone;
    private String phone2;
    private String fax;
    private long   created;
    private int    locationid;
    private String  role_name;
    private OnlineStatus  online_status;
    private WorkStatus    work_status;
    private long          lastOnLineTime;
    private int            dept_id;
    private String         desc;
    private String         shop_id;
    private String         dept_name;
    private int            bg_color_res;//颜色资源值

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getEmpcode() {
        return empcode;
    }

    public void setEmpcode(String empcode) {
        this.empcode = empcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRoleid() {
        return roleid;
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public int getLocationid() {
        return locationid;
    }

    public void setLocationid(int locationid) {
        this.locationid = locationid;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public OnlineStatus getOnline_status() {
        return online_status;
    }

    public void setOnline_status(OnlineStatus online_status) {
        this.online_status = online_status;
    }

    public WorkStatus getWork_status() {
        return work_status;
    }

    public void setWork_status(WorkStatus work_status) {
        this.work_status = work_status;
    }

    public long getLastOnLineTime() {
        return lastOnLineTime;
    }

    public void setLastOnLineTime(long lastOnLineTime) {
        this.lastOnLineTime = lastOnLineTime;
    }

    public int getDept_id() {
        return dept_id;
    }

    public void setDept_id(int dept_id) {
        this.dept_id = dept_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public int getBg_color_res() {
        return bg_color_res;
    }

    public void setBg_color_res(int bg_color_res) {
        this.bg_color_res = bg_color_res;
    }
}
