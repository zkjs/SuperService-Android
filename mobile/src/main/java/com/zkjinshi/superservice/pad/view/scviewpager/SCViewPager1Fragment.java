package com.zkjinshi.superservice.pad.view.scviewpager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import com.zkjinshi.superservice.pad.R;

/**
 * 开发者：dujiande
 * 日期：2015/10/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public  class SCViewPager1Fragment extends MyPageAnimation {

    public static final String TAG = SCViewPager1Fragment.class.getSimpleName();
    private int time = 300;

    public View view = null;

    public SCViewPager1Fragment(){

    }

    public void runInAnimation(){
        int[] ids = {R.id.p1,R.id.p2,R.id.p3,R.id.p4,R.id.p5,R.id.p6};
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
        int[] ids = {R.id.p1,R.id.p2,R.id.p3,R.id.p4,R.id.p5,R.id.p6};
        if(this.view == null){
            return;
        }
        for(int i=0;i<ids.length;i++) {
            View p1 = view.findViewById(ids[i]);
            p1.setAlpha(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_page1,null);
        this.view = view;
        runOutAnimation();
        Log.i(TAG, "onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
    }
}
