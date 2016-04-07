package com.zkjinshi.superservice.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.zkjinshi.base.util.BaseContext;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.vo.PayloadVo;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.yunba.android.manager.YunBaManager;

/**
 * 开发者：JimmyZhang
 * 日期：2016/1/4
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class YunBaSubscribeManager {

    public static final String TAG = YunBaSubscribeManager.class.getSimpleName();

    private YunBaSubscribeManager(){}

    private static YunBaSubscribeManager instance;

    public synchronized static YunBaSubscribeManager getInstance(){
        if(null == instance){
            instance = new YunBaSubscribeManager();
        }
        return instance;
    }

    /**
     * 订阅前端云巴推送(用户自选)
     * @param locIds
     */
    public void subscribe(String[] locIds){
        YunBaManager.subscribe(
            BaseContext.getInstance().getContext(),
            locIds,
            new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG,"订阅云巴成功");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (exception instanceof MqttException) {
                        MqttException ex = (MqttException)exception;
                        String msg =  "Subscribe failed with error code : " + ex.getReasonCode();
                        Log.i(TAG,"订阅云巴失败:"+msg);
                    }
                }
            }
        );
    }

    /**
     * 订阅频道
     * @param context
     */
    public void subscribe(final Context context){
        YunBaManager.getTopicList(context, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                JSONObject result = iMqttToken.getResult();
                try {
                    JSONArray topics = result.getJSONArray("topics");
                    if(null != topics && topics.length() > 0){
                        String[]  channels = new String[topics.length()];
                        for(int i=0; i <topics.length(); i++){
                            channels[i] = topics.get(i).toString();
                        }
                        subscribe(channels);
                    }

                } catch (JSONException e) {
                    Log.i(TAG, "获取云巴订阅频道 json解析异常:"+e.getMessage());
                }
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                if (throwable instanceof MqttException) {
                    MqttException ex = (MqttException)throwable;
                    String msg = TAG + "getTopicList failed with error code : " + ex.getReasonCode();
                    Log.i(TAG, "获取云巴订阅频道失败:"+msg);
                }
            }
        });
    }

    /**
     * 订阅后台云巴推送
     */
    public void subscribe(){
        String token = CacheUtil.getInstance().getExtToken();
        if(!TextUtils.isEmpty(token)){
            PayloadVo payloadVo = SSOManager.getInstance().decodeToken(token);
            if(null != payloadVo){
                String[] channels = SSOManager.getInstance().subscribeChannels(payloadVo);
                if(null != channels && channels.length > 0){
                    subscribe(channels);
                }
            }
        }
    }

    /**
     * 获取订阅频道
     * @param context
     */
    public void unSubscribe(final Context context,final  UnSubscribeCallback callback){
        YunBaManager.getTopicList(context, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                JSONObject result = iMqttToken.getResult();
                try {
                    JSONArray topics = result.getJSONArray("topics");
                    //当前存在订阅频道
                    if(null != topics && topics.length() > 0){
                        String[]  locIDs = new String[topics.length()];
                        for(int i=0; i <topics.length(); i++){
                            locIDs[i] = topics.get(i).toString();
                        }
                        YunBaManager.unsubscribe(
                                BaseContext.getInstance().getContext(),
                                locIDs,
                                new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        Log.i(TAG,"取消订阅云巴成功");
                                        if(null != callback){
                                            callback.onSuccess();
                                        }
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                        if (exception instanceof MqttException) {
                                            MqttException ex = (MqttException)exception;
                                            String msg =  "Subscribe failed with error code : " + ex.getReasonCode();
                                            Log.i(TAG,"取消订阅云巴失败:"+msg);
                                            if(null != callback){
                                                callback.onFailure();
                                            }
                                        }
                                    }
                                }
                        );
                    }

                } catch (JSONException e) {
                    Log.i(TAG, "获取云巴订阅频道 json解析异常:"+e.getMessage());
                    if(null != callback){
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                if (throwable instanceof MqttException) {
                    MqttException ex = (MqttException)throwable;
                    String msg = TAG + "getTopicList failed with error code : " + ex.getReasonCode();
                    Log.i(TAG, "获取云巴订阅频道失败:"+msg);
                    if(null != callback){
                        callback.onFailure();
                    }
                }
            }
        });
    }

    /**
     * 订阅别名
     * @param context
     */
    public void setAlias(final Context context){
        String tokenStr = CacheUtil.getInstance().getExtToken();
        PayloadVo payloadVo = SSOManager.getInstance().decodeToken(tokenStr);
        String alias = payloadVo.getSub();
        YunBaManager.setAlias(context, alias,
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(Constants.ZKJINSHI_BASE_TAG,"订阅别名成功");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception instanceof MqttException) {
                            MqttException ex = (MqttException)exception;
                            String msg =  "setAlias failed with error code : " + ex.getReasonCode();
                        }
                    }
                }
        );
    }

    /**
     * 取消订阅别名
     * @param context
     */
    public void cancelAlias(final Context context){
        YunBaManager.setAlias(context, "",
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(Constants.ZKJINSHI_BASE_TAG,"取消订阅别名成功");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception instanceof MqttException) {
                            MqttException ex = (MqttException)exception;
                            String msg =  "setAlias failed with error code : " + ex.getReasonCode();
                        }
                    }
                }
        );
    }

}
