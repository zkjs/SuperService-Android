package com.zkjinshi.superservice.activity.set;

import android.content.Context;

import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.ProtocolUtil;

import java.util.HashMap;

/**
 * 客人详情控制器
 * 开发者：vincent
 * 日期：2015/10/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientController {

    private ClientController(){}

    private static ClientController instance;

    public static synchronized ClientController getInstance(){
        if(null ==  instance){
            instance = new ClientController();
        }
        return instance;
    }

    /**
     * 获取用户详情
     * @param context
     * @param userID
     * @param token
     * @param shopID
     * @param phoneNumber
     * @param listener
     */
    public void getClientDetail(final Context context, String userID,
                                 String token, String shopID, String phoneNumber,
                                 ExtNetRequestListener listener) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getClientDetailUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("empid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        bizMap.put("phone", phoneNumber);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(listener);
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }
}
