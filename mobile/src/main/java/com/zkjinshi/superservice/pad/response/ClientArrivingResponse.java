package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.ClientArrivingVo;
import java.util.ArrayList;

/**
 * 开发者：qinyejun
 * 日期：2016/7/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientArrivingResponse extends BaseResponse {

    private ArrayList<ClientArrivingVo> data;

    public ArrayList<ClientArrivingVo> getData() {
        return data;
    }

    public void setData(ArrayList<ClientArrivingVo> data) {
        this.data = data;
    }
}
