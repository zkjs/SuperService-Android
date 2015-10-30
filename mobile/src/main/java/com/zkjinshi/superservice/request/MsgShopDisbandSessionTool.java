package com.zkjinshi.superservice.request;

import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.superservice.entity.MsgShopDisbandSession;
import com.zkjinshi.superservice.utils.CacheUtil;

/**
 * 开发者：JimmyZhang
 * 日期：2015/10/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgShopDisbandSessionTool {

    public static MsgShopDisbandSession buildMsgShopDisbandSession(String shopId,String sessionId){
        MsgShopDisbandSession msgShopDisbandSession = new MsgShopDisbandSession();
        msgShopDisbandSession.setType(ProtocolMSG.MSG_ShopDisbandSession);
        msgShopDisbandSession.setTimestamp(System.currentTimeMillis());
        msgShopDisbandSession.setFromid(CacheUtil.getInstance().getUserId());
        msgShopDisbandSession.setSessionid(sessionId);
        msgShopDisbandSession.setShopid(shopId);
        return msgShopDisbandSession;
    }
}
