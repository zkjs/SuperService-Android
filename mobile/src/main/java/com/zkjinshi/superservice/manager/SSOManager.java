package com.zkjinshi.superservice.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.Base64Decoder;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.PayloadVo;

import java.util.HashMap;

/**
 * 开发者：JimmyZhang
 * 日期：2016/2/26
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class SSOManager {

    public static final String TAG = SSOManager.class.getSimpleName();

    private SSOManager(){}

    private static SSOManager instance;

    public synchronized static SSOManager getInstance(){
        if(null == instance){
            instance = new SSOManager();
        }
        return instance;
    }

    /**
     * 获取token的负载信息
     * @param tokenStr
     * @return
     */
    public PayloadVo decodeToken(String tokenStr){
        //String tokenStr = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJjXzU2YTZlN2I1NjM0OGMiLCJ0eXBlIjozLCJleHBpcmUiOjE0NTY1NjE5NDI4MzcsInNob3BpZCI6Ijg4ODgiLCJyb2xlcyI6IltdIiwiZmVhdHVyZSI6IltdIn0.gPC-fUdKc-2gLGNvee6J9ZGVLXSJ96iVzZN47MsmO0z3PyQ4BMOq6CxVgIvFKyjeZx1Va_D8wphMSXByK8ppQtcQhPBv-q3CIFby8ttdE3y0yw6RXGrZnJwwusePPXBCAgXG80DtmWPjnjFRS5PVDpB3Ls3RQWPs5bSVTM0HkQ8";
        String[] tokenArr = tokenStr.split("\\.");
        PayloadVo payloadVo = null;
        if(null != tokenArr && tokenArr.length > 0){
            String payloadEncode = tokenArr[1];
            String payloadDecode = Base64Decoder.decode(payloadEncode);
            Log.i(Constants.ZKJINSHI_BASE_TAG,"token负载信息:"+payloadDecode);
            payloadVo = new Gson().fromJson(payloadDecode,PayloadVo.class);
        }
        return payloadVo;
    }

    /**
     * 获得角色相关频道
     * @return
     */
    public String[] subscribeChannels(PayloadVo payloadVo){
        String shopId = null;
        String[] roles = null;
        String[] channels = null;
        if(null != payloadVo){
            shopId = payloadVo.getShopid();
            if(!TextUtils.isEmpty(shopId)){
                roles =  payloadVo.getRoles();
                if(null != roles && roles.length > 0){
                    channels = new String[roles.length];
                    for(int i = 0; i< roles.length; i++){
                        channels[i] = shopId+"_" + roles[i];//“ShopID” + “_” + “RoleCode”
                    }
                    return channels;
                }
            }
        }
        return null;
    }

    /**
     * 是否是商家中心
     * @return
     */
    public boolean isShopCenter(){
        String token = CacheUtil.getInstance().getToken();
        if(!TextUtils.isEmpty(token)){
            PayloadVo payloadVo = decodeToken(token);
            if(null != payloadVo){
                String[] roles = payloadVo.getRoles();
                if(null != roles && roles.length > 0){
                    for (String  role: roles){
                        //SALE —-销售 SERCENTER---服务中心
                        if("SERCENTER".equals(role)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 刷新token
     */
    public void requestRefreshToken(final Context context){
        String url = ProtocolUtil.getTokenRefreshUrl();
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUT;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(context) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(Constants.ZKJINSHI_BASE_TAG, "errorCode:" + errorCode);
                Log.i(Constants.ZKJINSHI_BASE_TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(Constants.ZKJINSHI_BASE_TAG,"rawResult:"+result.rawResult);
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

}
