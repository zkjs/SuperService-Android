package com.zkjinshi.superservice.pad.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/7/5.
 */
public class GetDeptsVo implements Serializable {

    private int res;
    private String resDesc;
    private  ArrayList<DepartmentVo> data;

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }

    public ArrayList<DepartmentVo> getData() {
        return data;
    }

    public void setData(ArrayList<DepartmentVo> data) {
        this.data = data;
    }
}
