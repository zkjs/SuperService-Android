package com.zkjinshi.superservice.response;

import com.zkjinshi.superservice.bean.InviteCode;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/14
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeResponse extends BaseResponse {

    private ArrayList<InviteCode> data;
    private int count;

    public ArrayList<InviteCode> getData() {
        return data;
    }

    public void setData(ArrayList<InviteCode> data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
