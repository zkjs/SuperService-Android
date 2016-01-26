package com.zkjinshi.superservice.activity.common;

import android.content.Context;
import android.text.TextUtils;

import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.util.HashMap;

/**
 * 邀请码api使用控制器
 * 开发者：WinkyQin
 * 日期：2015/12/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeController {

    private final static String TAG = InviteCodeController.class.getSimpleName();

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
     * 获取已使用邀请码全部邀请记录
     * @param context
     * @param listener
     */
    public void getUsedCodeRecords(Context context, ExtNetRequestListener listener){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getCodeUserListUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token ", CacheUtil.getInstance().getToken());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(listener);
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /***
     * 获取邀请码列表
     * @param page
     * @param context
     * @param listener
     */
    public void getInviteCodes(int page, Context context, ExtNetRequestListener listener){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getInviteCodeUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("page", ""+page);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(listener);
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 为当前销售服务员生成邀请码
     * @param context
     * @param listener
     */
    public void newInviteCode(Context context, ExtNetRequestListener listener) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getNewRandomInviteCodeUrl());
        HashMap<String, String> bizMap = new HashMap<>();
        String shopID = CacheUtil.getInstance().getShopID();

        if(TextUtils.isEmpty(shopID)){
            return ;
        }

        bizMap.put("shopid", shopID);
        bizMap.put("salesid",  CacheUtil.getInstance().getUserId());
        bizMap.put("token",  CacheUtil.getInstance().getToken());

        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(listener);
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 获取所有的邀请码使用的记录
     * @param page
     * @param context
     * @param listener
     */
    public void getAllInviteCodeUsers(int page, Context context, ExtNetRequestListener listener) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getCodeUserAllUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("page", ""+page);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(listener);
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }
}
