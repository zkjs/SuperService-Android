package com.zkjinshi.superservice.activity.common.contact;

/**
 * 开发者：vincent
 * 日期：2015/9/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SortModelFactory {

    private static SortModelFactory instance = null;

    private SortModelFactory(){}

    public synchronized static SortModelFactory getInstance(){
        if(instance == null){
            instance = new SortModelFactory();
        }
        return instance;
    }



}
