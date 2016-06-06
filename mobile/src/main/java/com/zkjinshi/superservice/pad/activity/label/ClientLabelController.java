package com.zkjinshi.superservice.pad.activity.label;

import android.content.Context;

import com.zkjinshi.superservice.pad.net.ExtNetRequestListener;
import com.zkjinshi.superservice.pad.net.MethodType;
import com.zkjinshi.superservice.pad.net.NetResponse;
import com.zkjinshi.superservice.pad.net.NetRequest;
import com.zkjinshi.superservice.pad.net.NetRequestTask;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;

import org.json.JSONArray;

import java.util.HashMap;

/**
 * 开发者：JimmyZhang
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientLabelController {

    private ClientLabelController (){}

    private static ClientLabelController instance;

    public synchronized static ClientLabelController getInstance(){
        if(null == instance){
            instance = new ClientLabelController();
        }
        return instance;
    }

    /**
     * 获取用户标签列表
     * @param clientId
     * @param context
     * @param listener
     */
    public void requestGetClientTagsTask(String clientId, Context context, ExtNetRequestListener listener){
        String requestUrl = ProtocolUtil.getClientTagsUrl(clientId);
        NetRequest netRequest = new NetRequest(requestUrl);
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(listener);
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 更新用户标签
     * @param clientId
     * @param tagList
     * @param context
     * @param listener
     */
    public void requestUpdateClientTagsTask(String clientId, JSONArray tagList, final Context context, ExtNetRequestListener listener){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getUpdateClientTagsUrl());
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        HashMap<String,Object> bizMap = new HashMap<String,Object>();
        bizMap.put("si_id",clientId);
        bizMap.put("tags",tagList);
        netRequest.setObjectParamMap(bizMap);
        netRequestTask.methodType = MethodType.JSONPOST;
        netRequestTask.setNetRequestListener(listener);
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }
}
