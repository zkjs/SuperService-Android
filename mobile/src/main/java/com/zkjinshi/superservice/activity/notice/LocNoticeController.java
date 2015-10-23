package com.zkjinshi.superservice.activity.notice;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.superservice.adapter.ZoneAdapter;
import com.zkjinshi.superservice.bean.ZoneBean;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ZoneDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 开发者：JimmyZhang
 * 日期：2015/10/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LocNoticeController {

    public static final String TAG = LocNoticeController.class.getSimpleName();

    private static LocNoticeController instance;

    private Context context;

    private LocNoticeController(){}

    public synchronized static LocNoticeController getInstance(){
        if(null == instance){
            instance = new LocNoticeController();
        }
        return instance;
    }

    public LocNoticeController init(Context context){
        this.context = context;
        return this;
    }

    public void requestLocTask(){
        LogUtil.getInstance().info(LogLevel.INFO,"获取监听区域开始。。。");
        NetRequest netRequest = new NetRequest(ProtocolUtil.getZonelistUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token",CacheUtil.getInstance().getToken());
        bizMap.put("shopid",CacheUtil.getInstance().getShopID());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                LogUtil.getInstance().info(LogLevel.INFO, "获取监听区域结束。。。");
                try {
                    ArrayList<ZoneBean> zoneList = new Gson().fromJson(result.rawResult, new TypeToken<ArrayList<ZoneBean>>() {
                    }.getType());
                    if (null != zoneList && !zoneList.isEmpty()) {
                        ZoneDBUtil.getInstance().batchAddZone(zoneList);
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }
}
