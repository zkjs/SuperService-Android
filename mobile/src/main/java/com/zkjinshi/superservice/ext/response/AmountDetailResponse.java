package com.zkjinshi.superservice.ext.response;

import com.zkjinshi.superservice.ext.vo.AmountStatusVo;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/8
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AmountDetailResponse extends ExtBaseResponse {

    private String orderno;
    private AmountStatusVo data;

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public AmountStatusVo getData() {
        return data;
    }

    public void setData(AmountStatusVo data) {
        this.data = data;
    }
}
