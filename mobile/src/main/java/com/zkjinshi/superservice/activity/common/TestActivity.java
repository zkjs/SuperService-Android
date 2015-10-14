package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.widget.TextView;

import com.zkjinshi.superservice.R;

import com.zkjinshi.superservice.utils.JxlUtil;



/**
 * 测试用的Activity
 * 开发者：dujiande
 * 日期：2015/10/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TestActivity extends Activity{

    private final static String TAG = TestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
