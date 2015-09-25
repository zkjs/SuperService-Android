package com.zkjinshi.superservice.activity.common.contact;

/**
 * 客户联系人的类别
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum ContactType {

    LOCAL(0),//本地电话本联系人
    SERVER(1);//服务器上获取到联系人

    private ContactType(int value){
        this.value = value;
    }
    private int value;
    public int getValue(){
        return value;
    }

}
