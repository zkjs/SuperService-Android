package com.zkjinshi.superservice.response;

import com.zkjinshi.superservice.net.NetResponse;

/**
 * http响应基类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BaseExtResponse extends NetResponse {

    private boolean set = true;

    public boolean isSet() {
        return set;
    }
    public void setSet(boolean set) {
        this.set = set;
    }

}
