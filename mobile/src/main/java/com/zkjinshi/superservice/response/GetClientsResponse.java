package com.zkjinshi.superservice.response;

import com.zkjinshi.superservice.vo.ClientContactVo;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/11.
 */
public class GetClientsResponse extends BaseFornaxResponse {

    private ArrayList<ClientContactVo> data;

    public ArrayList<ClientContactVo> getData() {
        return data;
    }

    public void setData(ArrayList<ClientContactVo> data) {
        this.data = data;
    }
}
