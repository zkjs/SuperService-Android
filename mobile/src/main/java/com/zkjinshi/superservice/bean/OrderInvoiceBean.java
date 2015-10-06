package com.zkjinshi.superservice.bean;



import java.io.Serializable;

/**
 * 获取订单详细 发票信息实体
 * 开发者：dujiande
 * 日期：2015/10/06
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderInvoiceBean implements Serializable {

   private int id ;//            发票id int
   private String invoice_title ;//   发票抬头
   private String invoice_get_id ;//  取票方式 int

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoice_title() {
        return invoice_title;
    }

    public void setInvoice_title(String invoice_title) {
        this.invoice_title = invoice_title;
    }

    public String getInvoice_get_id() {
        return invoice_get_id;
    }

    public void setInvoice_get_id(String invoice_get_id) {
        this.invoice_get_id = invoice_get_id;
    }
}
