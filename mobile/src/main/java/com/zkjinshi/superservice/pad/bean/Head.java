package com.zkjinshi.superservice.pad.bean;

import java.io.Serializable;

/**
 * Http返回结果通用请求头
 * 开发者：WinkyQin
 * 日期：2015/11/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class Head implements Serializable{

    private boolean set;
    private int     err;
    private int     count;

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public boolean isSet() {
        return set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}