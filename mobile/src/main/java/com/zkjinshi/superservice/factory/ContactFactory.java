package com.zkjinshi.superservice.factory;

/**
 * 构建联系人工厂类
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactFactory {

    private static ContactFactory instance = null;

    private ContactFactory(){}

    public synchronized static ContactFactory getInstance(){
        if(instance == null){
            instance = new ContactFactory();
        }
        return instance;
    }
}
