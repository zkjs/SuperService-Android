package com.zkjinshi.superservice.pad.activity.chat.group.controller;

import android.content.Context;

import com.zkjinshi.superservice.pad.net.MethodType;
import com.zkjinshi.superservice.pad.net.NetRequest;
import com.zkjinshi.superservice.pad.net.NetRequestListener;
import com.zkjinshi.superservice.pad.net.NetRequestTask;
import com.zkjinshi.superservice.pad.net.NetResponse;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;

/**
 * 开发者：JimmyZhang
 * 日期：2015/12/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GroupMemberController {

    public static final String TAG = GroupMemberController.class.getSimpleName();

    private GroupMemberController(){}

    private static GroupMemberController instance;

    public synchronized static GroupMemberController getInstance(){
        if(null == instance){
            instance = new GroupMemberController();
        }
        return instance;
    }

    public void requestGroupMembersTask(final String groupId, final NetRequestListener netRequestListener, final Context context){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getGroupMemberUrl(groupId));
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        if(null != netRequestListener){
            netRequestTask.setNetRequestListener(netRequestListener);
        }
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

}
