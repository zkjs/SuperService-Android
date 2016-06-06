package com.zkjinshi.superservice.pad.vo;

/**
 * 客户联系人的类别
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum ContactType {

    UNNORMAL(0),   //非正式用户
    NORMAL(1);      //正式用户 已注册手机客户端的客户

    private ContactType(int value){
        this.value = value;
    }
    private int value;
    public int getValue(){
        return value;
    }

}
