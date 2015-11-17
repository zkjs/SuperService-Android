package com.zkjinshi.superservice.factory;

import android.text.TextUtils;
import android.util.Log;

import com.zkjinshi.superservice.bean.BookOrderBean;
import com.zkjinshi.superservice.bean.OrderDetailBean;
import com.zkjinshi.superservice.bean.OrderInvoiceBean;
import com.zkjinshi.superservice.bean.OrderPrivilegeBean;
import com.zkjinshi.superservice.bean.OrderRoomBean;
import com.zkjinshi.superservice.bean.OrderRoomTagBean;
import com.zkjinshi.superservice.bean.OrderUsersBean;

import java.util.ArrayList;
import java.util.List;

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

        if(!TextUtils.isEmpty(bookOrderBean.getPayment())){
            try{
                int id = Integer.parseInt(bookOrderBean.getPayment());
                orderRoomBean.setPay_id(id);
            }catch (Exception e){
                Log.e("OrderFactory",e.getMessage());
            }
        }

        orderRoomBean.setPay_status(bookOrderBean.getTradeStatus());
        orderDetailBean.setRoom(orderRoomBean);

        String manInStay   = bookOrderBean.getManInStay();
        String menInstay[] = manInStay.split(",");

        ArrayList<OrderUsersBean> orderUsers = new ArrayList<>();
        for(int i=0; i<menInstay.length; i++){
            OrderUsersBean user = new OrderUsersBean();
            user.setRealname(menInstay[i]);
            orderUsers.add(user);
        }

        orderDetailBean.setUsers(orderUsers);
        orderDetailBean.setInvoice(new OrderInvoiceBean());
        orderDetailBean.setRoom_tag(new ArrayList<OrderRoomTagBean>());
        orderDetailBean.setPrivilege(new ArrayList<OrderPrivilegeBean>());
        orderDetailBean.setContent(bookOrderBean.getContent());
        return orderDetailBean;
    }
}
