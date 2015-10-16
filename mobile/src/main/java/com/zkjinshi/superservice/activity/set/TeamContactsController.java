package com.zkjinshi.superservice.activity.set;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.listener.GetTeamContactsListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

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

    private final static String TAG = TeamContactsController.class.getSimpleName();

    private TeamContactsController(){}

    private static TeamContactsController instance;

    private Context  mContext;
    private Activity mActivity;

    public static synchronized TeamContactsController getInstance(){
        if(null ==  instance){
            instance = new TeamContactsController();
        }
        return instance;
    }

    public void init(Context context){
        this.mContext  = context;
        this.mActivity = (Activity)context;
    }

    public void getTeamContacts(final Context context, String userID,
                                  String token, final String shopID,
                                  final GetTeamContactsListener listener){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getTeamListUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new NetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {
                DialogUtil.getInstance().cancelProgressDialog();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                String jsonResult = result.rawResult;
                if (result.rawResult.contains("set") || jsonResult.contains("err")) {
                    return;
                } else {
                    Gson gson = new Gson();
                    List<TeamContactBean> teamContactBeans = gson.fromJson(jsonResult,
                            new TypeToken<ArrayList<TeamContactBean>>() {}.getType());

                    if (null != teamContactBeans) {
                        /** add to local db */
                        List<ShopEmployeeVo> shopEmployeeVos = ShopEmployeeFactory.getInstance().buildShopEmployees(teamContactBeans);
                        for (ShopEmployeeVo shopEmployeeVo : shopEmployeeVos) {
                            shopEmployeeVo.setShop_id(shopID);
                            ShopEmployeeDBUtil.getInstance().addShopEmployee(shopEmployeeVo);
                        }

                        if (null != listener) {
                            listener.getContactsDone(teamContactBeans);
                        }
                    }
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }
}
