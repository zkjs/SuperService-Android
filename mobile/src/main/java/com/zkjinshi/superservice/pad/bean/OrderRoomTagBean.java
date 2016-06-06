package com.zkjinshi.superservice.pad.bean;



import java.io.Serializable;

/**
 * 获取订单详细 房间标签实体
 * 开发者：dujiande
 * 日期：2015/10/06
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderRoomTagBean implements Serializable {
   private int id;//        房间选项id int
   private String content;//    房间选项名称
   private String sortorder;//  排序 int

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSortorder() {
        return sortorder;
    }

    public void setSortorder(String sortorder) {
        this.sortorder = sortorder;
    }
}
