package com.zkjinshi.superservice.bean;

import java.util.List;

/**
 * 查询邀请码记录对象
 * 开发者：WinkyQin
 * 日期：2015/11/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeData {

    private Head             head;
    private List<InviteCode> code_data;
    private Link             link;

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public List<InviteCode> getCode_data() {
        return code_data;
    }

    public void setCode_data(List<InviteCode> code_data) {
        this.code_data = code_data;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
