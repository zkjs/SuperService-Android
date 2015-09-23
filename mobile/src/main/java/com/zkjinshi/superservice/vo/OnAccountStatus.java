package com.zkjinshi.superservice.vo;

/**
 * 开发者：vincent
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum OnAccountStatus {

    ISONACCOUNT(0),
    NOTONACCOUNT(1);

    private OnAccountStatus(int value){
        this.value = value;
    }
    private int value;
    public int getValue(){
        return value;
    }
}
