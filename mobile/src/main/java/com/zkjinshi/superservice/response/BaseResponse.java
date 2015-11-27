package com.zkjinshi.superservice.response;

import com.zkjinshi.superservice.http.post.HttpResponse;

/**
 * http响应基类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BaseResponse extends HttpResponse {

    private boolean set = true;

    public boolean isSet() {
        return set;
    }
    public void setSet(boolean set) {
        this.set = set;
    }

}