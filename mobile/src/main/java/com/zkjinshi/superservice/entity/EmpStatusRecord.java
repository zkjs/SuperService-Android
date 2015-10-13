package com.zkjinshi.superservice.entity;

/**
 * 开发者：vincent
 * 日期：2015/10/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EmpStatusRecord {

//    type EmpStatus_Record struct {
//        EmpID        string
//        OnlineStatus uint32 `json:"onlinestatus"` // 0:在线 1:不在线
//        WorkStatus   uint32 `json:"workstatus"`   // 0:上班 1:下班
//    }

    private String  empid;
    private int     onlinestatus;//0:在线 1:不在线
    private int     workstatus;//0:上班 1:下班

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public int getOnlinestatus() {
        return onlinestatus;
    }

    public void setOnlinestatus(int onlinestatus) {
        this.onlinestatus = onlinestatus;
    }

    public int getWorkstatus() {
        return workstatus;
    }

    public void setWorkstatus(int workstatus) {
        this.workstatus = workstatus;
    }
}
