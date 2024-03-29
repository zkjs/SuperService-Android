package com.zkjinshi.superservice.activity.common;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.base.BaseFragmentActivity;
import com.zkjinshi.superservice.manager.YunBaSubscribeManager;
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
public class GuideActivity extends BaseFragmentActivity {

    private static String TAG = GuideActivity.class.getSimpleName();

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
        mDotsView.setDotRessource(R.mipmap.ellipse_pre, R.mipmap.ellipse_nor);
        mDotsView.setNumberOfPage(NUM_PAGES);

        mPageAdapter = new SCViewPagerAdapter(getSupportFragmentManager());
        mPageAdapter.setNumberOfPage(NUM_PAGES);
        mPageAdapter.setFragmentBackgroundColor(R.color.theme_100);
        mViewPager.setAdapter(mPageAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.i(TAG,"onPageScrolled("+position+","+positionOffset+","+positionOffsetPixels+")");
                if( positionOffset== 0.0 && positionOffset ==0){
                   mPageAdapter.runInAnimation(position);
                    if(position-1 >= 0){
                        mPageAdapter.runOutAnimation(position-1);
                    }
                    if(position+1 < NUM_PAGES){
                        mPageAdapter.runOutAnimation(position+1);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
               // Log.i(TAG,"onPageSelected("+position+")");
                mDotsView.selectDot(position);
                if(position > 1){
                    findViewById(R.id.people_main).setVisibility(View.INVISIBLE);
                }else{
                    findViewById(R.id.people_main).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
               // Log.i(TAG,"onPageScrollStateChanged("+state+")");
            }
        });

        final Point size = SCViewAnimationUtil.getDisplaySize(this);
        //第一页
//        View font1 = findViewById(R.id.page0_font);
//        SCViewAnimation font1Animation = new SCViewAnimation(font1);
//        font1Animation.addPageAnimation(new SCPositionAnimation(this, 0, 0, size.y/2));
//        mViewPager.addAnimation(font1Animation);
//
        View name = findViewById(R.id.page0_name);
        SCViewAnimation nameAnimation = new SCViewAnimation(name);
        nameAnimation.addPageAnimation(new SCPositionAnimation(this,0,0,-size.y/2));
        mViewPager.addAnimation(nameAnimation);
//
//        int distance = 0;
//        //第二页
//        View font2 = findViewById(R.id.page1_font);
//        SCViewAnimationUtil.prepareViewToGetSize(font2);
//        SCViewAnimation font2Animation = new SCViewAnimation(font2);
//        distance= (size.x + font2.getWidth())/2;
//        font2Animation.startToPosition(distance,null);
//        font2Animation.addPageAnimation(new SCPositionAnimation(this, 0,-distance,0));
//        font2Animation.addPageAnimation(new SCPositionAnimation(this, 1,0,size.y/2));
//        mViewPager.addAnimation(font2Animation);
//        //第三页
//        View font3 = findViewById(R.id.page2_font);
//        SCViewAnimationUtil.prepareViewToGetSize(font3);
//        SCViewAnimation font3Animation = new SCViewAnimation(font3);
//        distance= (size.x + font3.getWidth())/2;
//        font3Animation.startToPosition(distance,null);
//        font3Animation.addPageAnimation(new SCPositionAnimation(this, 1,-distance,0));
//        font3Animation.addPageAnimation(new SCPositionAnimation(this, 2,0,size.y/2));
//        mViewPager.addAnimation(font3Animation);
//
//        //第四页
//        View font4 = findViewById(R.id.page3_font);
//        SCViewAnimationUtil.prepareViewToGetSize(font4);
//        SCViewAnimation font4Animation = new SCViewAnimation(font4);
//        distance= (size.x + font4.getWidth())/2;
//        font4Animation.startToPosition(distance,null);
//        font4Animation.addPageAnimation(new SCPositionAnimation(this, 2,-distance,0));
//        font4Animation.addPageAnimation(new SCPositionAnimation(this, 3,0,size.y/2));
//        mViewPager.addAnimation(font4Animation);

    }

}
