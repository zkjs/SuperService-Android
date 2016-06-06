package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.MemberVo;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/16
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class MembersResponse extends BaseResponse {

    private ArrayList<MemberVo> data;

    public ArrayList<MemberVo> getData() {
        return data;
    }

    public void setData(ArrayList<MemberVo> data) {
        this.data = data;
    }
}
