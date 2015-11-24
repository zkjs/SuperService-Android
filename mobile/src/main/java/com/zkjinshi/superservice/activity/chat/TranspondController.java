package com.zkjinshi.superservice.activity.chat;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.bean.TeamContactBean;
import com.zkjinshi.superservice.factory.ShopEmployeeFactory;
import com.zkjinshi.superservice.listener.GetTeamContactsListener;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ShopEmployeeVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 消息转发控制器
 * 开发者：JimmyZhang
 * 日期：2015/11/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TranspondController extends AppCompatActivity {

    private final static String TAG = TranspondController.class.getSimpleName();

    private TranspondController(){}

    private static TranspondController instance;

    private Context mContext;
    private Activity mActivity;

    public static synchronized TranspondController getInstance(){
        if(null ==  instance){
            instance = new TranspondController();
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
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(context) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
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
                DialogUtil.getInstance().showProgressDialog(context);
            }
        });
        netRequestTask.execute();
    }
}
