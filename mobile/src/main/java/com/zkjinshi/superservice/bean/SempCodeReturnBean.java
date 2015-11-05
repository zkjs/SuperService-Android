package com.zkjinshi.superservice.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Http返回结果通用对象类
 * 开发者：WinkyQin
 * 日期：2015/11/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SempCodeReturnBean implements Serializable{

    private Head             head;
    private List<InviteCode> code_data;
    private Link             _link;

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

    public Link get_link() {
        return _link;
    }

    public void set_link(Link _link) {
        this._link = _link;
    }
}
