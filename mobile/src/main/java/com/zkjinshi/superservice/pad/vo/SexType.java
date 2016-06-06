package com.zkjinshi.superservice.pad.vo;

/**
 * 开发者：dujiande
 * 日期：2015/9/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum SexType {
    FEMALE(0), //女
    MALE(1);   //男

    private SexType(int value){
        this.value = value;
    }
    private final int value;
    public int getVlaue(){
        return value;
    }
}
