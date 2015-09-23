package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


import com.zkjinshi.superservice.R;

/**
 * 主页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MainActivity extends FragmentActivity {

    private void initView(){

    }

    private void initData(){
        //初始化抽屉按钮
        SlidingMenuManager.getInstance().initMenu(this);
    }

    private void initListeners(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListeners();
    }

}
