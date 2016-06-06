package com.zkjinshi.superservice.pad.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
    public class InviteCodeFragmentAdapter extends FragmentPagerAdapter{

    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    public InviteCodeFragmentAdapter(FragmentManager fm, List<Fragment> mFragmentList) {
        super(fm);
        this.fragmentList = mFragmentList;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
