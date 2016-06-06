package com.zkjinshi.superservice.pad.vo;

/**
 * 开发者：JimmyZhang
 * 日期：2015/10/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum ChatType {
    PRIVATE(1),//私聊
    COLLEAGUE(2),//同事群
    DISCUSS(3);//讨论组
    private ChatType(int value){
        this.value = value;
    }
    private final int value;
    public int getVlaue(){
        return value;
    }
}
