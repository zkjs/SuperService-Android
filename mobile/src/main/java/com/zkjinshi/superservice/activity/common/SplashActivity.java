package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.utils.CacheUtil;

/**
 * 应用启动页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SplashActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        LoginController.getInstance().init(this);

        if(CacheUtil.getInstance().isLogin()){
            LoginController.getInstance().requestLogin(CacheUtil.getInstance().getUserPhone());
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
        }
    }
}
