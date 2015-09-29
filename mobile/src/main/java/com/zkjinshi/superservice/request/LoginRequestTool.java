package com.zkjinshi.superservice.request;

import android.text.TextUtils;

import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.superservice.entity.MsgClientLogin;
import com.zkjinshi.superservice.utils.CacheUtil;

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
        return msgClientLogin;
    }
}
