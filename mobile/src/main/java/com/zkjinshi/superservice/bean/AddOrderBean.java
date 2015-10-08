package com.zkjinshi.superservice.bean;

/**
 * 添加订单返回实体
 * 开发者：dujiande
 * 日期：2015/10/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class AddOrderBean extends BaseBean{
    private String reservation_no;

    public String getReservation_no() {
        return reservation_no;
    }

    public void setReservation_no(String reservation_no) {
        this.reservation_no = reservation_no;
    }
}