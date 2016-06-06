package com.zkjinshi.superservice.pad.vo;

/**
 * 开发者：vincent
 * 日期：2015/10/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum  OnlineStatus {

    ONLINE(0),//在线
    OFFLINE(1);//离线

    private OnlineStatus(int value){
        this.value = value;
    }
    private int value;
    public int getValue(){
        return value;
    }

}
