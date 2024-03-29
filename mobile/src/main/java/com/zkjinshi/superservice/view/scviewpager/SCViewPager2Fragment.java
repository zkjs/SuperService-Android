package com.zkjinshi.superservice.view.scviewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import com.zkjinshi.superservice.R;

/**
 * 开发者：dujiande
 * 日期：2015/10/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SCViewPager2Fragment extends MyPageAnimation{

    public static final String TAG = SCViewPager2Fragment.class.getSimpleName();
    public View view;
    private int time = 300;

    public SCViewPager2Fragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_page2,null);
        Log.i(TAG, "onCreateView");
        this.view = view;
        runOutAnimation();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
    }

    public void runInAnimation(){
        int[] ids = {R.id.r1,R.id.r2,R.id.r3,R.id.r4,R.id.r5,R.id.r6};
        for(int i=0;i<ids.length;i++){
            View p1 = view.findViewById(ids[i]);
            p1.setAlpha(1);
            ScaleAnimation mScaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            mScaleAnimation.setDuration(time);
            mScaleAnimation.setFillAfter(true);

            AlphaAnimation mAlphaAnimation = new AlphaAnimation(0.1f, 1.0f);
            mAlphaAnimation.setDuration(time);
            mAlphaAnimation.setFillAfter(true);

            AnimationSet mAnimationSet=new AnimationSet(false);
            mAnimationSet.addAnimation(mScaleAnimation);
            mAnimationSet.addAnimation(mAlphaAnimation);
            mAnimationSet.setFillAfter(true);
            p1.setAnimation(mAnimationSet);
            p1.startAnimation(mAnimationSet);
        }
    }

    @Override
    public void runOutAnimation() {
        int[] ids = {R.id.r1,R.id.r2,R.id.r3,R.id.r4,R.id.r5,R.id.r6};
        if(this.view == null){
            return;
        }
        for(int i=0;i<ids.length;i++) {
            View p1 = view.findViewById(ids[i]);
            p1.setAlpha(0);
        }
    }
}
