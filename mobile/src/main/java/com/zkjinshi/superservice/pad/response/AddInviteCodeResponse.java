package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.SaleCodeVo;

/**
 * 获取邀请码
 * 开发者：JimmyZhang
 * 日期：2016/3/12
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AddInviteCodeResponse extends BaseResponse {

    private SaleCodeVo data;

    public SaleCodeVo getData() {
        return data;
    }

    public void setData(SaleCodeVo data) {
        this.data = data;
    }
}
