package com.zkjinshi.superservice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 开发者：JimmyZhang
 * 日期：2016/4/29
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class LabelGridView extends GridView {

    public LabelGridView(Context context) {
        super(context);
    }

    public LabelGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LabelGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
