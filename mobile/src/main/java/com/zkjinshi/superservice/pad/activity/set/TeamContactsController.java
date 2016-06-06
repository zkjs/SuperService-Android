package com.zkjinshi.superservice.pad.activity.set;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.pad.listener.GetTeamContactsListener;
import com.zkjinshi.superservice.pad.net.ExtNetRequestListener;
import com.zkjinshi.superservice.pad.net.MethodType;
import com.zkjinshi.superservice.pad.net.NetRequest;
import com.zkjinshi.superservice.pad.net.NetRequestTask;
import com.zkjinshi.superservice.pad.net.NetResponse;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.bean.TeamContactBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * 开发者：vincent
 * 日期：2015/10/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TeamContactsController {

    private static final String TAG = TeamContactsController.class.getSimpleName();

    private TeamContactsController(){}

    private static TeamContactsController instance;

    public static synchronized TeamContactsController getInstance(){
        if(null ==  instance){
            instance = new TeamContactsController();
        }
        return instance;
    }

    public void getTeamContacts(final Activity activity, String userID,
                                String token, final String shopID,
                                final GetTeamContactsListener listener){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getTeamListUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(activity, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(activity) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                if(null != listener){
                    listener.getContactsFailed();
                }
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().cancelProgressDialog();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                DialogUtil.getInstance().cancelProgressDialog();

                Log.i(TAG, "result.rawResult:" + result.rawResult);
                String jsonResult = result.rawResult;
                try {
                    Gson gson = new Gson();
                    List<TeamContactBean> teamContactBeans = gson.fromJson(jsonResult,
                            new TypeToken<ArrayList<TeamContactBean>>() {}.getType());

                    if (null != teamContactBeans) {
                        /** add to local db */
//                        List<EmployeeVo> shopEmployeeVos = ShopEmployeeFactory.getInstance().buildShopEmployees(teamContactBeans);
//                        if(null != shopEmployeeVos && !shopEmployeeVos.isEmpty()){
//                            if (null != listener) {
//                                listener.getContactsDone(shopEmployeeVos);
//                            }
//                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

   
}
