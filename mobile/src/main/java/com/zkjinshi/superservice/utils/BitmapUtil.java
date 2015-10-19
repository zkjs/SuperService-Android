package com.zkjinshi.superservice.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

/**
 * 开发者：vincent
 * 日期：2015/10/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BitmapUtil {

    public static Bitmap drawTextBitmap(String text, float textSize) {
        if(TextUtils.isEmpty(text))
            text = "";

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
}
