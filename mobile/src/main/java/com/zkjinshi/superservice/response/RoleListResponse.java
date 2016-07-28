package com.zkjinshi.superservice.response;

import com.zkjinshi.superservice.vo.AgencyVo;

import java.util.ArrayList;

/**
 * 获取商家部门响应体
 * 开发者：jimmyzhang
 * 日期：16/6/24
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class RoleListResponse extends BaseResponse {

    private ArrayList<AgencyVo> data;

    public ArrayList<AgencyVo> getData() {
        return data;
    }

    public void setData(ArrayList<AgencyVo> data) {
        this.data = data;
    }
}
