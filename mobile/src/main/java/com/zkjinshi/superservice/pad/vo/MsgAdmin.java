package com.zkjinshi.superservice.pad.vo;

/**
 * 开发者：vincent
 * 日期：2015/10/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum MsgAdmin {

    ISADMIN(0),//普通聊天成员
    NOTADMIN(1);//会话管理员

    private MsgAdmin(int value){
        this.value = value;
    }
    private final int value;

    public int getValue(){
        return value;
    }
}
