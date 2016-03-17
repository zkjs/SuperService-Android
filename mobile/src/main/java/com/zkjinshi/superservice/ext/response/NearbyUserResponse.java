package com.zkjinshi.superservice.ext.response;

import com.zkjinshi.superservice.ext.vo.AmountStatusVo;
import com.zkjinshi.superservice.ext.vo.NearbyUserVo;

import java.util.ArrayList;

/**
 * 开发者：jimmyzhang
 * 日期：2016/3/8
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class NearbyUserResponse extends ExtBaseResponse {

    private ArrayList<NearbyUserVo> data;

    public ArrayList<NearbyUserVo> getData() {
        return data;
    }

    public void setData(ArrayList<NearbyUserVo> data) {
        this.data = data;
    }
}
