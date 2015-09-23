package com.zkjinshi.superservice.activity.common;

/**
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SlidingMenuManager {

    private SlidingMenuManager(){}

    private static SlidingMenuManager instance;

    public synchronized static SlidingMenuManager getInstance(){
        if(null == instance){
            instance = new SlidingMenuManager();
        }
        return instance;
    }

    public void init(){

    }
}
