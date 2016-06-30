package com.zkjinshi.superservice.response;

import android.widget.BaseAdapter;

import com.zkjinshi.superservice.vo.EventVo;

import java.util.ArrayList;

/**
 * 获取活动列表
 * 开发者：jimmyzhang
 * 日期：16/6/30
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class EventListResponse extends BaseResponse {

    private ArrayList<EventVo> data;

    public ArrayList<EventVo> getData() {
        return data;
    }

    public void setData(ArrayList<EventVo> data) {
        this.data = data;
    }
}
