package com.zkjinshi.superservice.pad.vo;

/**
 * 开发者：JimmyZhang
 * 日期：2015/10/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum IdentityType {
    WAITER(0),//服务员
    BUSINESS(1);//商家
    private IdentityType(int value){
        this.value = value;
    }
    private final int value;
    public int getVlaue(){
        return value;
    }
}
