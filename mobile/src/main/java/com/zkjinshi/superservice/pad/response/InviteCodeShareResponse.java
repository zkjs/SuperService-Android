package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.InviteCodeShareVo;

/**
 * 获取邀请码分享链接响应
 * 开发者：JimmyZhang
 * 日期：2016/3/14
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeShareResponse extends BaseResponse {

    private InviteCodeShareVo data;

    public InviteCodeShareVo getData() {
        return data;
    }

    public void setData(InviteCodeShareVo data) {
        this.data = data;
    }
}
