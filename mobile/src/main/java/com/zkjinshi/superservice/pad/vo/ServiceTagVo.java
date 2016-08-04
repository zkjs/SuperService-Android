package com.zkjinshi.superservice.pad.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 服务标签实体
 * 开发者：jimmyzhang
 * 日期：16/6/23
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceTagVo implements Serializable {

    private String firstSrvTagName;
    private String firstSrvTagId;
    private ArrayList<SecondServiceTagVo> secondSrvTag;

    public String getFirstSrvTagName() {
        return firstSrvTagName;
    }

    public void setFirstSrvTagName(String firstSrvTagName) {
        this.firstSrvTagName = firstSrvTagName;
    }

    public String getFirstSrvTagId() {
        return firstSrvTagId;
    }

    public void setFirstSrvTagId(String firstSrvTagId) {
        this.firstSrvTagId = firstSrvTagId;
    }

    public ArrayList<SecondServiceTagVo> getSecondSrvTag() {
        return secondSrvTag;
    }

    public void setSecondSrvTag(ArrayList<SecondServiceTagVo> secondSrvTag) {
        this.secondSrvTag = secondSrvTag;
    }
}
