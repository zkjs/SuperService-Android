package com.zkjinshi.superservice.factory;

import com.zkjinshi.superservice.bean.BookOrderBean;
import com.zkjinshi.superservice.bean.OrderDetailBean;
import com.zkjinshi.superservice.bean.OrderInvoiceBean;
import com.zkjinshi.superservice.bean.OrderPrivilegeBean;
import com.zkjinshi.superservice.bean.OrderRoomBean;
import com.zkjinshi.superservice.bean.OrderRoomTagBean;
import com.zkjinshi.superservice.bean.OrderUsersBean;

import java.util.ArrayList;

/**
 * 开发者：dujiande
 * 日期：2015/10/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderFactory {

    private static OrderFactory orderFactory;

    private OrderFactory() {
    }

    public synchronized static OrderFactory getInstance(){
        if(null == orderFactory){
            orderFactory = new OrderFactory();
        }
        return orderFactory;
    }

    /**
     * 通过预定实体构建订单详细实体
     * @return
     */
    public OrderDetailBean buildOrderDetail(BookOrderBean bookOrderBean){
        OrderDetailBean orderDetailBean = new OrderDetailBean();

        OrderRoomBean orderRoomBean = new OrderRoomBean();
        orderRoomBean.setUserid(bookOrderBean.getUserID());
        orderRoomBean.setShopid(bookOrderBean.getShopID());
        orderRoomBean.setGuestid(bookOrderBean.getUserID());
        orderRoomBean.setGuest(bookOrderBean.getGuest());
        orderRoomBean.setGuesttel(bookOrderBean.getGuestTel());
        orderRoomBean.setCreated(bookOrderBean.getCreated());
        orderRoomBean.setFullname(bookOrderBean.getFullName());
        orderRoomBean.setRoom_typeid(Integer.parseInt(bookOrderBean.getRoomTypeID()));
        orderRoomBean.setRoom_type(bookOrderBean.getRoomType());
        orderRoomBean.setRooms(Integer.parseInt(bookOrderBean.getRooms()));
        orderRoomBean.setArrival_date(bookOrderBean.getArrivalDate());
        orderRoomBean.setDeparture_date(bookOrderBean.getDepartureDate());
        orderRoomBean.setRoom_rate(bookOrderBean.getRoomRate());
        orderRoomBean.setImgurl(bookOrderBean.getImage());
        orderRoomBean.setStatus(bookOrderBean.getStatus());
        orderRoomBean.setRemark(bookOrderBean.getRemark());
        orderRoomBean.setPayment(bookOrderBean.getPayment());
        orderRoomBean.setPay_status(bookOrderBean.getTradeStatus());

        orderDetailBean.setRoom(orderRoomBean);
        orderDetailBean.setUsers(new ArrayList<OrderUsersBean>());
        orderDetailBean.setInvoice(new OrderInvoiceBean());
        orderDetailBean.setRoom_tag(new ArrayList<OrderRoomTagBean>());
        orderDetailBean.setPrivilege(new ArrayList<OrderPrivilegeBean>());
        orderDetailBean.setContent(bookOrderBean.getContent());

        return orderDetailBean;
    }
}
