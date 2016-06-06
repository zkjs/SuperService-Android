package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.vo.GoodInfoVo;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/15.
 */
public class GetGoodListResponse extends BaseFornaxResponse {

    private ArrayList<GoodInfoVo> data;

    public ArrayList<GoodInfoVo> getData() {
        return data;
    }

    public void setData(ArrayList<GoodInfoVo> data) {
        this.data = data;
    }
}
