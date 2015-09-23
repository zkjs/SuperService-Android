package com.zkjinshi.superservice.activity.common.main;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.ViewPagerAdapter;
import com.zkjinshi.superservice.fragment.MessageFragment;
import com.zkjinshi.superservice.fragment.NoticeFragment;
import com.zkjinshi.superservice.fragment.OrderFragment;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ViewPagerManager {

    private ViewPagerManager(){}

    private static ViewPagerManager instance;

    private ArrayList<Fragment> fragmentList;
    private MessageFragment messageFragment;
    private NoticeFragment noticeFragment;
    private OrderFragment orderFragment;

    private ArrayList<String> titleList;

    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;

    private ViewPagerAdapter viewPagerAdapter;

    public synchronized static ViewPagerManager getInstance(){
        if(null == instance){
            instance = new ViewPagerManager();
        }
        return instance;
    }

    public void initViewPager(Context context){
        initView(context);
        initData(context);
    }

    private void initView(Context context){
        viewPager = (ViewPager)((Activity)context).findViewById(R.id.viewpager);
        pagerTabStrip = (PagerTabStrip)((Activity)context).findViewById(R.id.pagertab);
    }

    private void initData(Context context){
        fragmentList = new ArrayList<Fragment>();
        messageFragment = new MessageFragment();
        noticeFragment = new NoticeFragment();
        orderFragment = new OrderFragment();
        fragmentList.add(messageFragment);
        fragmentList.add(noticeFragment);
        fragmentList.add(orderFragment);
        titleList = new ArrayList<String>();
        titleList.add(context.getString(R.string.coming_notice));
        titleList.add(context.getString(R.string.message_notice));
        titleList.add(context.getString(R.string.deal_with_order));
        viewPagerAdapter = new ViewPagerAdapter(context,fragmentList,titleList);
        viewPager.setAdapter(viewPagerAdapter);
    }

}
