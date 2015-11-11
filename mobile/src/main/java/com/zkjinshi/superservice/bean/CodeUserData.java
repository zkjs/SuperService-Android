package com.zkjinshi.superservice.bean;

import java.util.List;

/**
 * 查询邀请码记录对象
 * 开发者：WinkyQin
 * 日期：2015/11/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CodeUserData {

    private Head                 head;
    private List<InviteCodeUser> data;
    private Link                 _link;

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public List<InviteCodeUser> getData() {
        return data;
    }

    public void setData(List<InviteCodeUser> data) {
        this.data = data;
    }

    public Link get_link() {
        return _link;
    }

    public void set_link(Link _link) {
        this._link = _link;
    }
}
