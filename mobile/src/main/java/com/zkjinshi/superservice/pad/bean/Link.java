package com.zkjinshi.superservice.pad.bean;

import java.io.Serializable;

/**
 * 开发者：WinkyQin
 * 日期：2015/11/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class Link implements Serializable{

    private int pageStar;
    private int pageEnd;

    public int getPageStar() {
        return pageStar;
    }

    public void setPageStar(int pageStar) {
        this.pageStar = pageStar;
    }

    public int getPageEnd() {
        return pageEnd;
    }

    public void setPageEnd(int pageEnd) {
        this.pageEnd = pageEnd;
    }
}
