package com.zkjinshi.superservice.pad.manager;

/**
 * 开发者：JimmyZhang
 * 日期：2016/1/4
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneManager {

    public static final String TAG = ZoneManager.class.getSimpleName();

    private ZoneManager(){}

    private static ZoneManager instance;

    public synchronized static ZoneManager getInstance(){
        if(null == instance){
            instance = new ZoneManager();
        }
        return instance;
    }

}
