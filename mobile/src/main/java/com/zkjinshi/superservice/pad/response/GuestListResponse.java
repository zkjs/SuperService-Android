package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.GuestVo;

import java.util.ArrayList;

/**
 * 获取邀请名单
 * 开发者：jimmyzhang
 * 日期：16/6/30
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class GuestListResponse extends BaseResponse {

    private ArrayList<GuestVo> data;

    public ArrayList<GuestVo> getData() {
        return data;
    }

    public void setData(ArrayList<GuestVo> data) {
        this.data = data;
    }
}
