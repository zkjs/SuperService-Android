package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.os.Bundle;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.utils.CacheUtil;

/**
 * 团队联系人显示界面
 * 开发者：vincent
 * 日期：2015/10/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamContactsActivity extends Activity{

    private final static String TAG = TeamContactsActivity.class.getSimpleName();

    private String mUserID;
    private String mShopID;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_contacts);

        initView();
        initData();
        initListener();
        loadContacts();
    }

    private void initView() {

    }

    private void initData() {
        mUserID     = CacheUtil.getInstance().getUserId();
        mShopID     = CacheUtil.getInstance().getShopID();
        mToken      = CacheUtil.getInstance().getToken();
    }

    private void initListener() {
        
    }

    private void loadContacts() {
        
    }

}
