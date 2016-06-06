package com.zkjinshi.superservice.pad.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientTagVo implements Serializable {

    private ArrayList<ItemTagVo> tags;
    private int canoptcnt;

    public ArrayList<ItemTagVo> getTags() {
        return tags;
    }

    public void setTags(ArrayList<ItemTagVo> tags) {
        this.tags = tags;
    }

    public int getCanoptcnt() {
        return canoptcnt;
    }

    public void setCanoptcnt(int canoptcnt) {
        this.canoptcnt = canoptcnt;
    }

}
