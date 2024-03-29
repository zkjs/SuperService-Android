package com.zkjinshi.superservice.utils;

import com.zkjinshi.superservice.R;

import java.util.Random;

/**
 * 开发者：vincent
 * 日期：2015/10/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class RandomDrawbleUtil {

    private static int[] drawableRes = new int[] {
              R.drawable.bg_circle_blue, R.drawable.bg_circle_gray,
              R.drawable.bg_circle_purple, R.drawable.bg_circle_steelblue,
              R.drawable.bg_circle_red, R.drawable.bg_circle_orange };

    public static int getRandomDrawable(){
        return drawableRes[new Random().nextInt(drawableRes.length)];
    }

    public static int getDrawableByIndex(int index){
        return drawableRes[index % drawableRes.length];
    }

}
