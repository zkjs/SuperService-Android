package com.zkjinshi.superservice.activity.label;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

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
