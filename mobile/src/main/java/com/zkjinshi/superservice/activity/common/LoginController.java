package com.zkjinshi.superservice.activity.common;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ShopDepartmentDBUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.DepartmentVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 开发者：dujiande
 * 日期：2015/9/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginController {

    private final static String TAG = LoginController.class.getSimpleName();

    private LoginController(){}
    private static LoginController instance;
    private Context context;
    private Activity activity;

    public static synchronized LoginController getInstance(){
        if(null ==  instance){
            instance = new LoginController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.activity = (Activity)context;

    }

    /**
     * 服务员请求登录
     * @param phone
     */
    public void requestLogin(final String phone, boolean isLoading, ExtNetRequestListener netRequestListener){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getSempLoginUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("phone", phone);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(activity,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        if(null != netRequestListener){
            netRequestTask.setNetRequestListener(netRequestListener);
        }
        netRequestTask.isShowLoadingDialog = isLoading;
        netRequestTask.execute();
    }

    /**
     * 管理员请求登录
     * @param phone
     */
    public void requestAdminLogin(final String phone, final String password, boolean isLoading, ExtNetRequestListener netRequestListener){
        String url = ProtocolUtil.getAdminLoginUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("phone",phone);
        bizMap.put("password", password);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(activity,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        if(null != netRequestListener){
            netRequestTask.setNetRequestListener(netRequestListener);
        }
        netRequestTask.isShowLoadingDialog = isLoading;
        netRequestTask.execute();
    }

    /**
     * 获取部门列表
     * @param userID
     * @param token
     * @param shopID
     */
    public void getDeptList(String userID, String token, final String shopID) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getDeptListUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener() {
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
                String jsonResult = result.rawResult;
                if (result.rawResult.contains("set") || jsonResult.contains("err")) {
                    return ;
                } else {
                    Gson gson = new Gson();
                    List<DepartmentVo> departmentVos = gson.fromJson(jsonResult,
                            new TypeToken<ArrayList<DepartmentVo>>() {}.getType());
                    /** add to local db */
                    if(null != departmentVos && departmentVos.isEmpty()){
                        ShopDepartmentDBUtil.getInstance().batchAddShopDepartments(departmentVos);
                    }
                }
            }

            @Override
            public void beforeNetworkRequestStart()     {
                //网络请求前
            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

}
