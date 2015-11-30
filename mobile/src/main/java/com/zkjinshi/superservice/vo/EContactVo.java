package com.zkjinshi.superservice.vo;

import java.io.Serializable;

/**
 * 环信联系人实体
 * 开发者：JimmyZhang
 * 日期：2015/11/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EContactVo implements Serializable {

    private String contactId;
    private String contactName;

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

}
