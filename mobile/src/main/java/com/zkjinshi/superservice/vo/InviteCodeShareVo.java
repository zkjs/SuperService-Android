package com.zkjinshi.superservice.vo;

import java.io.Serializable;

/**
 * 生成邀请码链接实体
 * 开发者：JimmyZhang
 * 日期：2016/3/14
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeShareVo implements Serializable {

    private String joinpage;

    public String getJoinpage() {
        return joinpage;
    }

    public void setJoinpage(String joinpage) {
        this.joinpage = joinpage;
    }
}
