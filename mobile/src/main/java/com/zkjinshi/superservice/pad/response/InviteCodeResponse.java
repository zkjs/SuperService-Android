package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.InviteCodeListVo;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/14
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeResponse extends BaseResponse {

    private InviteCodeListVo data;

    public InviteCodeListVo getData() {
        return data;
    }

    public void setData(InviteCodeListVo data) {
        this.data = data;
    }
}
