package com.zkjinshi.superservice.pad.ext.response;

import com.zkjinshi.superservice.pad.ext.vo.AmountStatusVo;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/8
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AmountRecordResponse extends ExtBaseResponse {

    private ArrayList<AmountStatusVo> data;

    public ArrayList<AmountStatusVo> getData() {
        return data;
    }

    public void setData(ArrayList<AmountStatusVo> data) {
        this.data = data;
    }
}
