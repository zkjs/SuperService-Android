package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.WhiteUserVo;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2016/5/24
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */

public class WhiteUserListResponse extends BaseResponse {

    private ArrayList<WhiteUserVo> data;

    public ArrayList<WhiteUserVo> getData() {
        return data;
    }

    public void setData(ArrayList<WhiteUserVo> data) {
        this.data = data;
    }
}
