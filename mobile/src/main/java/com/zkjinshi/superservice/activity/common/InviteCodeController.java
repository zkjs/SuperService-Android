package com.zkjinshi.superservice.activity.common;

import android.content.Context;

import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.ProtocolUtil;

/**
 * 邀请码api使用控制器
 * 开发者：WinkyQin
 * 修改着：JimmyZhang
 * 日期：2015/12/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeController {


    private static InviteCodeController instance;

    private InviteCodeController(){

    }

    public synchronized static InviteCodeController getInstance(){
        if(null == instance){
            instance = new InviteCodeController();
        }
        return instance;
    }

    /***
     * 获取新增邀请码列表
     * @param page
     * @param context
     * @param listener
     */
    public void getNewInviteCodes(int page, Context context, ExtNetRequestListener listener){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getSaleCodeListUrl("0","15",""+page));
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(listener);
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

    /**
     * 获取邀请码已使用的记录
     * @param page
     * @param context
     * @param listener
     */
    public void getHistoryInviteCodes(int page, Context context, ExtNetRequestListener listener) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getSaleCodeListUrl("1","15",""+page));
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(listener);
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }
}
