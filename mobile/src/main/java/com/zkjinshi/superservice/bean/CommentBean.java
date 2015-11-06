package com.zkjinshi.superservice.bean;

import java.io.Serializable;

/**
 * 开发者：dujiande
 * 日期：2015/11/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CommentBean implements Serializable {

    private HeadBean head;
    private CommentDataBean data;

    public HeadBean getHead() {
        return head;
    }

    public void setHead(HeadBean head) {
        this.head = head;
    }

    public CommentDataBean getData() {
        return data;
    }

    public void setData(CommentDataBean data) {
        this.data = data;
    }
}
