package com.zkjinshi.superservice.vo;

import com.zkjinshi.superservice.bean.InviteCode;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/14
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeListVo implements Serializable {

    private ArrayList<InviteCode> salecodes;
    private int total;

    public ArrayList<InviteCode> getSalecodes() {
        return salecodes;
    }

    public void setSalecodes(ArrayList<InviteCode> salecodes) {
        this.salecodes = salecodes;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
