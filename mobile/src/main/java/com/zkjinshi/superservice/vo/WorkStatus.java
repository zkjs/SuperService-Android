package com.zkjinshi.superservice.vo;

/**
 * 上班枚举类
 * 开发者：vincent
 * 日期：2015/10/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum WorkStatus {

    ONWORK(0),//上班
    OFFWORK(1);//下班

    private WorkStatus(int value){
        this.value = value;
    }
    private int value;
    public int getValue(){
        return value;
    }

}
