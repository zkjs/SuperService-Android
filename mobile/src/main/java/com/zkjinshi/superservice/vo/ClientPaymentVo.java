package com.zkjinshi.superservice.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientPaymentVo implements Serializable {

    private int amount;
    private String createtime;
    private String orderno;
    private String remark;
    private String userid;

    public int getAmount() { return amount; }

    public void setAmount(int amount) { this.amount = amount;}

    public String getCreatetime() { return createtime; }

    public void setCreatetime(String createtime) { this.createtime = createtime; }

    public String getOrderno() { return orderno; }

    public void setOrderno(String orderno) { this.orderno = orderno; }

    public String getRemark() { return remark; }

    public void setRemark(String remark) { this.remark = remark; }

    public String getUserid() { return userid; }

    public void setUserid(String userid) { this.userid = userid; }

}
