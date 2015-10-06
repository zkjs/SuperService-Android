package com.zkjinshi.superservice.bean;



import java.io.Serializable;
import java.util.ArrayList;

/**
 * 获取订单详细 返回实体
 * 开发者：dujiande
 * 日期：2015/10/06
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderDetailBean implements Serializable {

    private OrderRoomBean room;//订房信息
    private ArrayList<OrderUsersBean> users;//入住人信息
    private OrderInvoiceBean invoice;//发票信息
    private ArrayList<OrderRoomTagBean> room_tag;//房间标签
    private ArrayList<OrderPrivilegeBean> privilege;//服务特权
    private String user_applevel;
    private String user_shoplevel;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OrderRoomBean getRoom() {
        return room;
    }

    public void setRoom(OrderRoomBean room) {
        this.room = room;
    }

    public ArrayList<OrderUsersBean> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<OrderUsersBean> users) {
        this.users = users;
    }

    public OrderInvoiceBean getInvoice() {
        return invoice;
    }

    public void setInvoice(OrderInvoiceBean invoice) {
        this.invoice = invoice;
    }

    public ArrayList<OrderRoomTagBean> getRoom_tag() {
        return room_tag;
    }

    public void setRoom_tag(ArrayList<OrderRoomTagBean> room_tag) {
        this.room_tag = room_tag;
    }

    public ArrayList<OrderPrivilegeBean> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(ArrayList<OrderPrivilegeBean> privilege) {
        this.privilege = privilege;
    }

    public String getUser_applevel() {
        return user_applevel;
    }

    public void setUser_applevel(String user_applevel) {
        this.user_applevel = user_applevel;
    }

    public String getUser_shoplevel() {
        return user_shoplevel;
    }

    public void setUser_shoplevel(String user_shoplevel) {
        this.user_shoplevel = user_shoplevel;
    }
}
