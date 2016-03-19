package com.zkjinshi.superservice.ext.vo;

import java.io.Serializable;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/8
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AmountStatusVo implements Serializable {

    /**
     "userid": "8888",   //用户id
     "username": "王小小",  //用户姓名
     "createtime": "2016-03-13 08:00:00",    //收款时间
     "amount": 231,  //支付金额
     "orderno": "H321ieau231",   //订单号
     "paymentno": "T231282312",  //支付单号
     "status": "0",  //订单状态码
     "statusdesc": "已确认",    //订单状态描述
     "confirmtime": "2016-03-13 08:00:00"    //最后确认时间: 失败时间/成功时间
     */

    private String userid;
    private String username;
    private String userimage;
    private String createtime;
    private long amount;
    private String orderno;
    private String paymentno;
    private int status;//订单状态码  0-待确认, 1-已拒绝, 2-已确认
    private String statusdesc;
    private String confirmtime;

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getPaymentno() {
        return paymentno;
    }

    public void setPaymentno(String paymentno) {
        this.paymentno = paymentno;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusdesc() {
        return statusdesc;
    }

    public void setStatusdesc(String statusdesc) {
        this.statusdesc = statusdesc;
    }

    public String getConfirmtime() {
        return confirmtime;
    }

    public void setConfirmtime(String confirmtime) {
        this.confirmtime = confirmtime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }
}
