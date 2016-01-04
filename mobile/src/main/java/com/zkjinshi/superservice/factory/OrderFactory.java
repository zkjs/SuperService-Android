package com.zkjinshi.superservice.factory;

import android.text.TextUtils;
import android.util.Log;

import com.zkjinshi.superservice.bean.BookOrderBean;
import com.zkjinshi.superservice.bean.OrderDetailBean;

import org.w3c.dom.Text;

import java.sql.Date;

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
        String orderNO = bookOrderBean.getReservationNO();
        if(!TextUtils.isEmpty(orderNO)){
            orderDetailBean.setOrderno(orderNO);
        }

        orderDetailBean.setShopid(bookOrderBean.getShopID());
        orderDetailBean.setUserid(bookOrderBean.getUserID());
        orderDetailBean.setUsername(bookOrderBean.getGuest());
        orderDetailBean.setTelephone(bookOrderBean.getGuestTel());
        String created = bookOrderBean.getCreated();
        if(!TextUtils.isEmpty(created)){
            orderDetailBean.setCreated(Date.parse(created));
        }
        orderDetailBean.setShopname(bookOrderBean.getFullName());
        orderDetailBean.setProductid(bookOrderBean.getRoomTypeID());
        orderDetailBean.setRoomtype(bookOrderBean.getRoomType());

        String rooms = bookOrderBean.getRooms();
        if (!TextUtils.isEmpty(rooms)){
            orderDetailBean.setRoomcount(Integer.parseInt(rooms));
        }
        orderDetailBean.setArrivaldate(bookOrderBean.getArrivalDate());
        orderDetailBean.setLeavedate(bookOrderBean.getDepartureDate());

        String roomRate = bookOrderBean.getRoomRate();
        if(!TextUtils.isEmpty(roomRate)){
            orderDetailBean.setRoomprice(Float.valueOf(roomRate));
        }

        orderDetailBean.setImgurl(bookOrderBean.getImage());
        orderDetailBean.setOrderstatus(bookOrderBean.getStatus());
        orderDetailBean.setRemark(bookOrderBean.getRemark());

        if(!TextUtils.isEmpty(bookOrderBean.getPayment())){
            try{
                int id = Integer.parseInt(bookOrderBean.getPayment());
                orderDetailBean.setPaytype(id);
            }catch (Exception e){
                Log.e("OrderFactory",e.getMessage());
            }
        }

//        orderDetailBean.setPay_status(bookOrderBean.getTradeStatus());
//        orderDetailBean.setRoom(orderDetailBean);
//        String manInStay = bookOrderBean.getManInStay();
//        //加入非空判断
//        if(!TextUtils.isEmpty(manInStay)){
//            String menInstay[] = manInStay.split(",");
//            ArrayList<OrderUsersBean> orderUsers = new ArrayList<>();
//            for(int i=0; i<menInstay.length; i++){
//                OrderUsersBean user = new OrderUsersBean();
//                user.setRealname(menInstay[i]);
//                orderUsers.add(user);
//            }
//            orderDetailBean.setUsers(orderUsers);
//        }
//        orderDetailBean.setInvoice(new OrderInvoiceBean());
//        orderDetailBean.setRoom_tag(new ArrayList<OrderRoomTagBean>());
//        orderDetailBean.setPrivilege(new ArrayList<OrderPrivilegeBean>());
//        orderDetailBean.setContent(bookOrderBean.getContent());
        return orderDetailBean;
    }
}