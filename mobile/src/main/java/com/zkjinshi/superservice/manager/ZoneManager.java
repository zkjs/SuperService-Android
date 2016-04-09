package com.zkjinshi.superservice.manager;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.BaseContext;
import com.zkjinshi.superservice.bean.ZoneBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.HashMap;

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
