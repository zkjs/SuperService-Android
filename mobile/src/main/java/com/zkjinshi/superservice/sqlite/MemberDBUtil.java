package com.zkjinshi.superservice.sqlite;

import android.content.Context;

import com.zkjinshi.superservice.ServiceApplication;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MemberDBUtil {

    private final static String TAG = MemberDBUtil.class.getSimpleName();

    private Context      context;
    private DBOpenHelper helper;

    private static MemberDBUtil instance;

    private MemberDBUtil(){};

    public synchronized static MemberDBUtil getInstance(){
        if(null == instance){
            instance = new MemberDBUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {
        context = ServiceApplication.getContext();
        helper  = new DBOpenHelper(context);
    }
















}
