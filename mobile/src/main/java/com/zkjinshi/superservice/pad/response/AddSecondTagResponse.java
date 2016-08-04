package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.SecondServiceTagVo;

/**
 * 添加二级标签响应体
 * 开发者：jimmyzhang
 * 日期：16/6/24
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AddSecondTagResponse extends BaseResponse {

    private SecondServiceTagVo data;

    public SecondServiceTagVo getData() {
        return data;
    }

    public void setData(SecondServiceTagVo data) {
        this.data = data;
    }
}