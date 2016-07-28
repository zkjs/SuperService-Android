package com.zkjinshi.superservice.response;

import com.zkjinshi.superservice.vo.ClientArrivingVo;
import com.zkjinshi.superservice.vo.ClientPaymentVo;

import java.util.ArrayList;

/**
 * 开发者：qinyejun
 * 日期：2016/7/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientPaymentResponse extends BaseResponse {

    private ArrayList<ClientPaymentVo> data;

    public ArrayList<ClientPaymentVo> getData() {
        return data;
    }

    public void setData(ArrayList<ClientPaymentVo> data) {
        this.data = data;
    }
}
