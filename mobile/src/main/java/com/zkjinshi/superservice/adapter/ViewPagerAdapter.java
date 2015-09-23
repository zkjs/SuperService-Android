package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zkjinshi.superservice.R;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentList;
    private Context context;
    private ArrayList<String> titleList;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public ViewPagerAdapter(Context context,ArrayList<Fragment> fragmentList) {
        this(((FragmentActivity)context).getSupportFragmentManager());
        this.fragmentList = fragmentList;
        this.context = context;
    }

    public ViewPagerAdapter(Context context,ArrayList<Fragment> fragmentList,ArrayList<String> titleList) {
        this(context,fragmentList);
        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
