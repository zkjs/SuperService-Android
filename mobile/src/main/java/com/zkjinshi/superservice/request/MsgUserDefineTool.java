package com.zkjinshi.superservice.request;

import com.google.gson.Gson;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.superservice.entity.MsgUserDefine;
import com.zkjinshi.superservice.entity.Order;

/**
 * 订单推送实体工具类
 * 开发者：JimmyZhang
 * 日期：2015/9/1
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgUserDefineTool {

    /**
     MsgUserDefine msgUserDefine = MsgUserDefineTool.buildSuccMsgUserDefine(mUserID, clientID, reservationNO, mShopID);
     Gson gson = new Gson();
     String jsonMsg = gson.toJson(msgUserDefine);
     WebSocketManager.getInstance().sendMessage(jsonMsg);
     */

    /**
     * 构建成功订单推送实体
     * @return
     */
    public static MsgUserDefine buildSuccMsgUserDefine(String fromId,String toId,String orderId, String shopId){
        MsgUserDefine msgUserDefine = new MsgUserDefine();
        msgUserDefine.setChildtype(1003);
        msgUserDefine.setType(ProtocolMSG.MSG_UserDefine);
        msgUserDefine.setTimestamp(System.currentTimeMillis());
        msgUserDefine.setPushalert("订单确认");
        Order order = new Order();
        order.setOrderId(orderId);
        order.setShopId(shopId);
        Gson gson = new Gson();
        String jsonOrder = gson.toJson(order);
        msgUserDefine.setContent(jsonOrder);//对象 1.shopID 2.orderID
        msgUserDefine.setPushofflinemsg(0);
        msgUserDefine.setFromid(fromId);
        msgUserDefine.setToid(toId);
        return msgUserDefine;
    }

    /**
     String clientID = bookOrder.getUserid();
     MsgUserDefineTool.buildFailMsgUserDefine(mUserID, clientID);
     */

    /**
     * 构建失败订单推送实体
     * @return
     */
    public static MsgUserDefine buildFailMsgUserDefine(String fromId,String toId){
        MsgUserDefine msgUserDefine = new MsgUserDefine();
        msgUserDefine.setChildtype(1002);
        msgUserDefine.setType(ProtocolMSG.MSG_UserDefine);
        msgUserDefine.setTimestamp(System.currentTimeMillis());
        msgUserDefine.setPushalert("订单确认");
        msgUserDefine.setContent("订单创建失败");
        msgUserDefine.setPushofflinemsg(0);
        msgUserDefine.setFromid(fromId);
        msgUserDefine.setToid(toId);
        return msgUserDefine;
    }

}
