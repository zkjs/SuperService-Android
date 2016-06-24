package com.zkjinshi.superservice.vo;

import java.io.Serializable;

/**
 * 二级服务标签实体
 * 开发者：jimmyzhang
 * 日期：16/6/23
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class SecondServiceTagVo implements Serializable {

    private String secondSrvTagName;
    private String secondSrvTagId;
    private String secondSrvTagDesc;

    public String getSecondSrvTagName() {
        return secondSrvTagName;
    }

    public void setSecondSrvTagName(String secondSrvTagName) {
        this.secondSrvTagName = secondSrvTagName;
    }

    public String getSecondSrvTagId() {
        return secondSrvTagId;
    }

    public void setSecondSrvTagId(String secondSrvTagId) {
        this.secondSrvTagId = secondSrvTagId;
    }

    public String getSecondSrvTagDesc() {
        return secondSrvTagDesc;
    }

    public void setSecondSrvTagDesc(String secondSrvTagDesc) {
        this.secondSrvTagDesc = secondSrvTagDesc;
    }
}
