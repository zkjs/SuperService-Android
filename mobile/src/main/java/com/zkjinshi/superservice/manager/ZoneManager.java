package com.zkjinshi.superservice.manager;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.BaseContext;
import com.zkjinshi.superservice.bean.ZoneBean;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 开发者：JimmyZhang
 * 日期：2016/1/4
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ZoneManager {

    public static final String TAG = ZoneManager.class.getSimpleName();

    private ZoneManager(){}

    private static ZoneManager instance;

    public synchronized static ZoneManager getInstance(){
        if(null == instance){
            instance = new ZoneManager();
        }
        return instance;
    }

    public void requestMyZoneTask(){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getMySemplocationUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token",CacheUtil.getInstance().getToken());
        bizMap.put("shopid",CacheUtil.getInstance().getShopID());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(BaseContext.getInstance().getContext(),netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(BaseContext.getInstance().getContext()) {
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
                super.onNetworkResponseSucceed(result);

                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try{
                    ArrayList<ZoneBean> zoneList = new Gson().fromJson(result.rawResult, new TypeToken< ArrayList<ZoneBean>>(){}.getType());
                    if(null != zoneList && !zoneList.isEmpty()){
                        CacheUtil.getInstance().saveListCache("zoneBeanList",zoneList);
                    }
                }catch (Exception e){
                    Log.e(TAG,e.getMessage());
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
