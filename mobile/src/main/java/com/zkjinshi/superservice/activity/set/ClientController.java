package com.zkjinshi.superservice.activity.set;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.bean.ClientDetailBean;
import com.zkjinshi.superservice.factory.ClientFactory;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ClientDBUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ClientVo;
import com.zkjinshi.superservice.vo.ContactType;
import com.zkjinshi.superservice.vo.OnlineStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 客人详情控制器
 * 开发者：vincent
 * 日期：2015/10/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientController {

    private static final String TAG = ClientController.class.getSimpleName();

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

    /**
     * 获取我的客户列表
     * @param userID
     * @param token
     * @param shopID
     * @param listener
     */
    public void getShopClients(Context context, String userID,
                               String token, final String shopID,
                               final ExtNetRequestListener listener){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getShopUserListUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType     = MethodType.PUSH;
        netRequestTask.setNetRequestListener(listener);
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 获取我的客户列表并添加到本地数据库
     * @param userID
     * @param token
     * @param shopID
     */
    public void getShopClients(Context context, String userID,
                               String token, final String shopID){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getShopUserListUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType     = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener() {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                super.onNetworkRequestError(errorCode, errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {
                super.onNetworkRequestCancelled();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                DialogUtil.getInstance().cancelProgressDialog();
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                String jsonResult = result.rawResult;
                if (result.rawResult.contains("set") || jsonResult.contains("err")) {
//                    DialogUtil.getInstance().showToast(mContext, "用户操作权限不够，请重新登录。");
                } else {
                    Gson gson = new Gson();
                    List<ClientDetailBean> clientDetailBeans = gson.fromJson(jsonResult,
                            new TypeToken<ArrayList<ClientDetailBean>>() {}.getType());

                    if (null != clientDetailBeans && !clientDetailBeans.isEmpty()) {
                        List<ClientVo> clientVos = ClientFactory.getInstance().buildClientVosByClientBeans(clientDetailBeans);

                        for (ClientVo clientVo : clientVos) {
                            clientVo.setContactType(ContactType.NORMAL);
                            clientVo.setIsOnline(OnlineStatus.OFFLINE);

                            if (!ClientDBUtil.getInstance().isClientExistByUserID(clientVo.getUserid())) {
                                ClientDBUtil.getInstance().addClient(clientVo);
                            }
                        }
                    }
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                super.beforeNetworkRequestStart();
            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }
}
