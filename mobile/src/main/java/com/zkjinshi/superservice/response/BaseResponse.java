package com.zkjinshi.superservice.response;

import java.io.Serializable;

/**
 * Http网络请求基类
 * 开发者：JimmyZhang
 * 日期：2016/3/12
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BaseResponse implements Serializable {

    private int res;
    private String resDesc;

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
}
