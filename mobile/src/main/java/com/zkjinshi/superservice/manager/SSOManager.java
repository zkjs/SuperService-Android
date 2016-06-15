package com.zkjinshi.superservice.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.activity.common.LoginActivity;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.response.BasePavoResponse;
import com.zkjinshi.superservice.utils.AsyncHttpClientUtil;
import com.zkjinshi.superservice.utils.Base64Decoder;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.PayloadVo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

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
        String[] tokenArr = tokenStr.split("\\.");
        PayloadVo payloadVo = null;
        if(null != tokenArr && tokenArr.length > 0){
            String payloadEncode = tokenArr[1];
            String payloadDecode = Base64Decoder.decode(payloadEncode);
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
     * 是否有收款权限
     * @return
     */
    public boolean isCollection(){
        String token = CacheUtil.getInstance().getExtToken();
        if(!TextUtils.isEmpty(token)){
            PayloadVo payloadVo = decodeToken(token);
            if(null != payloadVo){
                String[] roles = payloadVo.getRoles();
                if(null != roles && roles.length > 0){
                    for (String  role: roles){
                        //SALE —-销售 SERCENTER---服务中心
                        if("POS".equals(role)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 是否是商家中心
     * @return
     */
    public boolean isShopCenter(){
        String token = CacheUtil.getInstance().getExtToken();
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
    public void requestRefreshToken(final Context context,final SSOCallBack ssoCallBack){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout( com.zkjinshi.superservice.utils.Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            final String url = ProtocolUtil.getTokenRefreshUrl();
            client.put(context,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    //DialogUtil.getInstance().showAvatarProgressDialog(context,"");
                }

                public void onFinish(){
                    //DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        BasePavoResponse basePavoResponse = new Gson().fromJson(response,BasePavoResponse.class);
                        if(null != basePavoResponse){
                            int restult = basePavoResponse.getRes();
                            if(0 == restult){
                                String token = basePavoResponse.getToken();
                                if(!TextUtils.isEmpty(token)){
                                    CacheUtil.getInstance().setExtToken(token);
                                    PayloadVo payloadVo = decodeToken(token);
                                    if(null != payloadVo){
                                        Set<String> featureSet = payloadVo.getFeatures();
                                        if(null != featureSet){
                                            CacheUtil.getInstance().setFeatures(featureSet);
                                        }
                                    }
                                    if(null != ssoCallBack){
                                        ssoCallBack.onNetworkResponseSucceed();
                                    }
                                }
                            }else{
                                String errorMsg = basePavoResponse.getResDesc();
                                if(!TextUtils.isEmpty(errorMsg)){
                                    DialogUtil.getInstance().showCustomToast(context,errorMsg, Gravity.CENTER);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure( context,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(context,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public interface SSOCallBack{
        public void onNetworkResponseSucceed();
    }

}
