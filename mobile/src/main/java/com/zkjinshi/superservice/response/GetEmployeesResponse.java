package com.zkjinshi.superservice.response;

import com.zkjinshi.superservice.vo.ClientContactVo;
import com.zkjinshi.superservice.vo.EmployeeVo;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/11.
 */
public class GetEmployeesResponse extends BaseFornaxResponse {

    private ArrayList<EmployeeVo> data;

    public ArrayList<EmployeeVo> getData() {
        return data;
    }

    public void setData(ArrayList<EmployeeVo> data) {
        this.data = data;
    }
}
