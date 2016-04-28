package com.zkjinshi.superservice.vo;

import java.io.Serializable;

/**
 * 喜好标签实体
 * 开发者：JimmyZhang
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ItemTagVo implements Serializable{

    private int tagid;
    private String tagname;
    private String tagcode;
    private int count;
    private int isopt;

    public int getTagid() {
        return tagid;
    }

    public void setTagid(int tagid) {
        this.tagid = tagid;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public String getTagcode() {
        return tagcode;
    }

    public void setTagcode(String tagcode) {
        this.tagcode = tagcode;
    }

    public int getIsopt() {
        return isopt;
    }

    public void setIsopt(int isopt) {
        this.isopt = isopt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
