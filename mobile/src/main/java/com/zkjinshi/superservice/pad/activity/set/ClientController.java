package com.zkjinshi.superservice.pad.activity.set;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.pad.factory.ClientFactory;
import com.zkjinshi.superservice.pad.net.ExtNetRequestListener;
import com.zkjinshi.superservice.pad.net.MethodType;
import com.zkjinshi.superservice.pad.net.NetRequest;
import com.zkjinshi.superservice.pad.net.NetRequestTask;
import com.zkjinshi.superservice.pad.net.NetResponse;
import com.zkjinshi.superservice.pad.utils.ProtocolUtil;
import com.zkjinshi.superservice.pad.vo.ClientVo;
import com.zkjinshi.superservice.pad.vo.ContactType;
import com.zkjinshi.superservice.pad.vo.OnlineStatus;
import com.zkjinshi.superservice.pad.bean.ClientDetailBean;
import com.zkjinshi.superservice.pad.sqlite.ClientDBUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 客人网络请求控制器
 * 开发者：WinkyQin
 * 日期：2015/10/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ClientController {

    private static final String TAG = ClientController.class.getSimpleName();

    private ClientController(){}

    private static ClientController instance;

    public static synchronized ClientController getInstance(){
        if(null == instance){
            instance = new ClientController();
        }
        return instance;
    }

    /**
     * 获取用户详情
     * @param context
     * @param userID
     * @param listener
     */
    public void getClientInfo(Context context, String userID,
                              String token, String clientID,
                              ExtNetRequestListener listener) {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getClientInfoUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("userid", userID);
        bizMap.put("token", token);
        bizMap.put("find_userid", clientID);
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
                               String token, String shopID,
                               ExtNetRequestListener listener){

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
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(context) {
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
                try {
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    String jsonResult = result.rawResult;
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
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
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

    /**
     * 查询用户是否是邀请码和指定商家是否绑定销售
     * @param phoneNumber
     */
    public void getBindUserInfo(Context context,
                                String shopID, String phoneNumber,
                                ExtNetRequestListener requestListener) {

        NetRequest netRequest = new NetRequest(ProtocolUtil.getBindUserInfo());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("shopid", shopID);
        bizMap.put("phone", phoneNumber);
        netRequest.setBizParamMap(bizMap);

        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(requestListener);
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 服务员 主动绑定客户
     * @param context
     * @param fuid //待绑定客户ID
     * @param userID //当前服务员ID
     * @param token
     * @param requestListener
     */
    public void addFuser(Context context, String shopID, String fuid, String userID,
                         String token, ExtNetRequestListener requestListener) {

        NetRequest netRequest = new NetRequest(ProtocolUtil.getAddFuserUrl());
        HashMap<String,String> bizMap = new HashMap<>();
        bizMap.put("fuid", fuid);
        bizMap.put("salesid", userID);
        bizMap.put("token", token);
        bizMap.put("shopid", shopID);
        netRequest.setBizParamMap(bizMap);

        NetRequestTask netRequestTask = new NetRequestTask(context, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(requestListener);
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

}
