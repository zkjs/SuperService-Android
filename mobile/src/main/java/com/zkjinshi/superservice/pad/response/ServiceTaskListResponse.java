package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.ServiceTaskVo;

import java.util.ArrayList;

/**
 * 呼叫服务列表响应类
 * 开发者：jimmyzhang
 * 日期：16/6/23
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceTaskListResponse extends BaseResponse {

    private ArrayList<ServiceTaskVo> data;

    public ArrayList<ServiceTaskVo> getData() {
        return data;
    }

    public void setData(ArrayList<ServiceTaskVo> data) {
        this.data = data;
    }
}