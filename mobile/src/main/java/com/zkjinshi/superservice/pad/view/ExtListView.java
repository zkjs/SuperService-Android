package com.zkjinshi.superservice.pad.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 开发者：jimmyzhang
 * 日期：16/6/25
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ExtListView extends ListView {

    public ExtListView(Context context) {
        super(context);
    }

    public ExtListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}