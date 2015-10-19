package com.zkjinshi.superservice.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.fragment.MessageFragment;
import com.zkjinshi.superservice.fragment.NoticeFragment;
import com.zkjinshi.superservice.fragment.OrderFragment;

/**
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    public static final int NUM_ITEMS = 3;
    public static final int ALL_POS = 0;
    public static final int SHARED_POS = 1;
    public static final int FAVORITES_POS = 2;

    private Context context;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ALL_POS:
                return NoticeFragment.newInstance();
            case SHARED_POS:
                return MessageFragment.newInstance();
            case FAVORITES_POS:
                return OrderFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case ALL_POS:
                return context.getString(R.string.coming_notice);
            case SHARED_POS:
                return context.getString(R.string.message_notice);
            case FAVORITES_POS:
                return context.getString(R.string.deal_with_order);
            default:
                return "";
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
