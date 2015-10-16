package com.zkjinshi.superservice.request;

import android.text.TextUtils;

import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.superservice.entity.MsgClientLogin;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.IdentityType;

/**
 * 登录请求帮助类
 * 开发者：JimmyZhang
 * 日期：2015/8/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginRequestTool {

    /**
     * 构建协议登录对象
     * @return
     */
    public static MsgClientLogin buildLoginRequest() {
        String userID   = CacheUtil.getInstance().getUserId();
        String userName = CacheUtil.getInstance().getUserName();
        MsgClientLogin msgClientLogin = new MsgClientLogin();
        msgClientLogin.setType(ProtocolMSG.MSG_ClientLogin);
        msgClientLogin.setTimestamp(System.currentTimeMillis());
        if(!TextUtils.isEmpty(userID)){
            msgClientLogin.setId(userID);
        }
        if(!TextUtils.isEmpty(userName)){
            msgClientLogin.setName(userName);
        }
        msgClientLogin.setLogintype(1);
        msgClientLogin.setVersion("");
        msgClientLogin.setPlatform("");
        msgClientLogin.setAppid("");
        msgClientLogin.setShopid("120");//TODO JimmyZhang 写死，后期需要做修改
        if(CacheUtil.getInstance().getLoginIdentity().equals(IdentityType.WAITER)){
            msgClientLogin.setLoc(CacheUtil.getInstance().getAreaInfo());
        }else{
            msgClientLogin.setLoc("1,3,2,6");
        }
        msgClientLogin.setRoleid(1);//TODO JimmyZhang 写死，后期需要做修改
        return msgClientLogin;
    }
}
