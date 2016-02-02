package com.zkjinshi.superservice.manager;

import android.content.Context;
import android.util.Log;

import com.zkjinshi.base.util.BaseContext;
import com.zkjinshi.superservice.utils.CacheUtil;

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

    private boolean isSubscribed = false;

    public synchronized static YunBaSubscribeManager getInstance(){
        if(null == instance){
            instance = new YunBaSubscribeManager();
        }
        return instance;
    }

    /**
     * 云巴推送注册
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
                    isSubscribed = true;
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

//    /**
//     * 取消云巴订阅
//     */
//    public void unSubscribe(){
//        try {
//            String locIds = CacheUtil.getInstance().getAreaInfo();
//            String[] zoneArray = locIds.split(",");
//            if(null != zoneArray && zoneArray.length > 0){
//                YunBaManager.unsubscribe(
//                    BaseContext.getInstance().getContext(),
//                    zoneArray,
//                    new IMqttActionListener() {
//                        @Override
//                        public void onSuccess(IMqttToken asyncActionToken) {
//                            Log.i(TAG,"取消订阅云巴成功");
//                            isSubscribed = false;
//                        }
//
//                        @Override
//                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                            if (exception instanceof MqttException) {
//                                MqttException ex = (MqttException)exception;
//                                String msg =  "Subscribe failed with error code : " + ex.getReasonCode();
//                                Log.i(TAG,"取消订阅云巴失败:"+msg);
//                            }
//                        }
//                    }
//                );
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 当前是否注册订阅频道
     * @return
     */
    public boolean isSubscribed() {
        return isSubscribed;
    }

        /**
     * 获取订阅频道
     * @param context
     */
    public void unSubscribe(final Context context){
        YunBaManager.getTopicList(context, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                JSONObject result = iMqttToken.getResult();
                try {
                    JSONArray topics = result.getJSONArray("topics");
                    String[]  locIDs = topics.join(",").split(",");

                    //当前存在订阅频道
                    if(null != locIDs && locIDs.length > 0){
                        YunBaManager.unsubscribe(
                                BaseContext.getInstance().getContext(),
                                locIDs,
                                new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        isSubscribed = false;
                                        Log.i(TAG,"取消订阅云巴成功");
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                        isSubscribed = false;
                                        if (exception instanceof MqttException) {
                                            MqttException ex = (MqttException)exception;
                                            String msg =  "Subscribe failed with error code : " + ex.getReasonCode();
                                            Log.i(TAG,"取消订阅云巴失败:"+msg);
                                        }
                                    }
                                }
                        );
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
}
