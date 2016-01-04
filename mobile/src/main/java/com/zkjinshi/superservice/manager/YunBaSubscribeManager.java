package com.zkjinshi.superservice.manager;

import android.util.Log;

import com.zkjinshi.base.util.BaseContext;
import com.zkjinshi.superservice.factory.ZoneFactory;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

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
     * 取消云巴订阅
     */
    public void unSubscribe(){
        String[] zoneArray = ZoneFactory.getInstance().buildZoneArray();
        if(null != zoneArray && zoneArray.length > 0){
            YunBaManager.unsubscribe(BaseContext.getInstance().getContext(),zoneArray,
                    new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.i(TAG,"取消订阅云巴成功");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            if (exception instanceof MqttException) {
                                MqttException ex = (MqttException)exception;
                                String msg =  "Subscribe failed with error code : " + ex.getReasonCode();
                                Log.i(TAG,"取消订阅云巴失败:"+msg);
                            }
                        }
                    }
            );
        }
    }
}
