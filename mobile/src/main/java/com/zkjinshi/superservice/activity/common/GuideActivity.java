package com.zkjinshi.superservice.activity.common;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.view.scviewpager.DotsView;
import com.zkjinshi.superservice.view.scviewpager.SCPositionAnimation;
import com.zkjinshi.superservice.view.scviewpager.SCViewAnimation;
import com.zkjinshi.superservice.view.scviewpager.SCViewAnimationUtil;
import com.zkjinshi.superservice.view.scviewpager.SCViewPager;
import com.zkjinshi.superservice.view.scviewpager.SCViewPagerAdapter;



/**
 * 开发者：dujiande
 * 日期：2015/10/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GuideActivity  extends FragmentActivity {

    private static final int NUM_PAGES = 4;

    private SCViewPager mViewPager;
    private SCViewPagerAdapter mPageAdapter;
    private DotsView mDotsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);

        mViewPager = (SCViewPager) findViewById(R.id.viewpager_main_activity);
        mDotsView = (DotsView) findViewById(R.id.dotsview_main);
        mDotsView.setDotRessource(R.drawable.ellipse_pre, R.drawable.ellipse_nor);
        mDotsView.setNumberOfPage(NUM_PAGES);

        mPageAdapter = new SCViewPagerAdapter(getSupportFragmentManager());
        mPageAdapter.setNumberOfPage(NUM_PAGES);
        mPageAdapter.setFragmentBackgroundColor(R.color.theme_100);
        mViewPager.setAdapter(mPageAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mDotsView.selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        final Point size = SCViewAnimationUtil.getDisplaySize(this);

//        View atSkex = findViewById(R.id.imageview_main_activity_at_skex);
//        SCViewAnimationUtil.prepareViewToGetSize(atSkex);
//        SCViewAnimation atSkexAnimation = new SCViewAnimation(atSkex);
//        atSkexAnimation.addPageAnimation(new SCPositionAnimation(getApplicationContext(), 0, 0, -(size.y - atSkex.getHeight())));
//        atSkexAnimation.addPageAnimation(new SCPositionAnimation(getApplicationContext(), 1, -size.x, 0));
//        mViewPager.addAnimation(atSkexAnimation);
//
//        View mobileView = findViewById(R.id.imageview_main_activity_mobile);
//        SCViewAnimation mobileAnimation = new SCViewAnimation(mobileView);
//        mobileAnimation.startToPosition((int)(size.x*1.5), null);
//        mobileAnimation.addPageAnimation(new SCPositionAnimation(this, 0, -(int)(size.x*1.5), 0));
//        mobileAnimation.addPageAnimation(new SCPositionAnimation(this, 1, -(int)(size.x*1.5), 0));
//        mViewPager.addAnimation(mobileAnimation);

        View bg1 = findViewById(R.id.bg1);
        SCViewAnimation bg1TagAnimation = new SCViewAnimation(bg1);
        bg1TagAnimation.addPageAnimation(new SCPositionAnimation(this, 0,-size.x,0));
        mViewPager.addAnimation(bg1TagAnimation);

        View bg2 = findViewById(R.id.bg2);
        SCViewAnimation bg2TagAnimation = new SCViewAnimation(bg2);
        bg2TagAnimation.startToPosition(size.x,null);
        bg2TagAnimation.addPageAnimation(new SCPositionAnimation(this, 0,-size.x,0));
        bg2TagAnimation.addPageAnimation(new SCPositionAnimation(this, 1,-size.x,0));
        mViewPager.addAnimation(bg2TagAnimation);

        View bg3 = findViewById(R.id.bg3);
        SCViewAnimation bg3TagAnimation = new SCViewAnimation(bg3);
        bg3TagAnimation.startToPosition(size.x,null);
        bg3TagAnimation.addPageAnimation(new SCPositionAnimation(this, 1,-size.x,0));
        bg3TagAnimation.addPageAnimation(new SCPositionAnimation(this, 2,-size.x,0));
        mViewPager.addAnimation(bg3TagAnimation);

        View bg4 = findViewById(R.id.bg4);
        SCViewAnimation bg4TagAnimation = new SCViewAnimation(bg4);
        bg4TagAnimation.startToPosition(size.x,null);
        bg4TagAnimation.addPageAnimation(new SCPositionAnimation(this, 2,-size.x,0));
        bg4TagAnimation.addPageAnimation(new SCPositionAnimation(this, 3,-size.x,0));
        mViewPager.addAnimation(bg4TagAnimation);


    }

    private void goLogin() {
        Intent loginIntent = new Intent(GuideActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }

}
