package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.ClientTagVo;

/**
 * 开发者：JimmyZhang
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientTagResponse extends BaseResponse {

    private ClientTagVo data;

    public ClientTagVo getData() {
        return data;
    }

    public void setData(ClientTagVo data) {
        this.data = data;
    }
}
