package com.zkjinshi.superservice.pad.response;

import com.zkjinshi.superservice.pad.bean.ZoneBean;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/16.
 */
public class GetZoneListResponse extends BaseFornaxResponse {

    private ArrayList<ZoneBean> data;

    public ArrayList<ZoneBean> getData() {
        return data;
    }

    public void setData(ArrayList<ZoneBean> data) {
        this.data = data;
    }
}
