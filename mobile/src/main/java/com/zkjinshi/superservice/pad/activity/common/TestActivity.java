package com.zkjinshi.superservice.pad.activity.common;

import android.os.Bundle;
import android.os.Environment;

import com.zkjinshi.superservice.pad.utils.JxlUtil;
import com.zkjinshi.superservice.pad.R;

import com.zkjinshi.superservice.pad.base.BaseActivity;


/**
 * 测试用的Activity
 * 开发者：dujiande
 * 日期：2015/10/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TestActivity extends BaseActivity {

    private final static String TAG = TestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
        initData();
        initListener();
    }

    private void initView() {
    }

    private void initData() {

        String path = Environment.getExternalStorageDirectory().getPath()+"/test2.xlsx";
        String content = JxlUtil.readXLSX(path);
    }

    private void initListener() {

    }
}
