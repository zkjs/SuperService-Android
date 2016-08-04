package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.ServiceTagVo;

import java.util.ArrayList;

/**
 * 服务标签列表响应体
 * 开发者：jimmyzhang
 * 日期：16/6/23
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceTagListResponse extends BaseResponse {

    private ArrayList<ServiceTagVo> data;

    public ArrayList<ServiceTagVo> getData() {
        return data;
    }

    public void setData(ArrayList<ServiceTagVo> data) {
        this.data = data;
    }
}
