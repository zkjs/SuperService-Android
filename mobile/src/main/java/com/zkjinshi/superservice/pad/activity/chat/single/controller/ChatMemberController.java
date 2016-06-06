package com.zkjinshi.superservice.pad.activity.chat.single.controller;

import android.content.Context;

import com.zkjinshi.superservice.pad.net.MethodType;
import com.zkjinshi.superservice.pad.net.NetRequest;
import com.zkjinshi.superservice.pad.net.NetRequestListener;
import com.zkjinshi.superservice.pad.net.NetRequestTask;
import com.zkjinshi.superservice.pad.net.NetResponse;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/16
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatMemberController {

    public static final String TAG = ChatMemberController.class.getSimpleName();

    private ChatMemberController(){}

    private static ChatMemberController instance;

    public static synchronized ChatMemberController getInstance(){
        if(null == instance){
            instance = new ChatMemberController();
        }
        return instance;
    }

    public void requestChatMembersTask(final String userId, final NetRequestListener netRequestListener, final Context context){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getUsersInfoUrl(userId));
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        if(null != netRequestListener){
            netRequestTask.setNetRequestListener(netRequestListener);
        }
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }
}
