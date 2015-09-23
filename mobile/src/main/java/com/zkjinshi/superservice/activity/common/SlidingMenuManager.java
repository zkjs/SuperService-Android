package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.fragment.SetFragment;

/**
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SlidingMenuManager {

    private SlidingMenuManager(){}

    private static SlidingMenuManager instance;

    private SlidingMenu slidingMenu;

    private Fragment fragment;

    public synchronized static SlidingMenuManager getInstance(){
        if(null == instance){
            instance = new SlidingMenuManager();
        }
        return instance;
    }

    public void initMenu(Context context) {
        slidingMenu = new SlidingMenu(context);
        fragment = new SetFragment();
        slidingMenu.attachToActivity((Activity) context, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(((Activity) context).getLayoutInflater().inflate(R.layout.left_menu_frame, null));
        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_left_menu_frame, fragment).commit();
        slidingMenu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置滑动菜单视图的宽度
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setSecondaryShadowDrawable(R.drawable.shadow);
    }


}
