package com.zkjinshi.superservice.vo;

/**
 * 开发者：JimmyZhang
 * 日期：2015/10/14
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EmpStatusVo {

    private String  empId;//员工id
    private String empName;//员工姓名
    private int onlineStatus;//0:在线 1:不在线
    private int workStatus;//0:上班 1:下班
    private long loginTimestamp;//最近上线时间

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public int getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(int workStatus) {
        this.workStatus = workStatus;
    }

    public long getLoginTimestamp() {
        return loginTimestamp;
    }

    public void setLoginTimestamp(long loginTimestamp) {
        this.loginTimestamp = loginTimestamp;
    }
}
