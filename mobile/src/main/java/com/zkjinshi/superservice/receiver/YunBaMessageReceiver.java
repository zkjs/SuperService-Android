package com.zkjinshi.superservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.bean.BookOrderBean;
import com.zkjinshi.superservice.bean.LocPushBean;
import com.zkjinshi.superservice.bean.NoticeBean;
import com.zkjinshi.superservice.entity.MsgEmpStatusCountRSP;
import com.zkjinshi.superservice.entity.MsgPushTriggerLocNotificationM2S;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.notification.NotificationHelper;
import com.zkjinshi.superservice.sqlite.ComingDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.Constants;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.ComingVo;

import org.json.JSONException;

import java.util.HashMap;

import io.yunba.android.manager.YunBaManager;

/**
 * 开发者：JimmyZhang
 * 日期：2015/12/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class YunBaMessageReceiver extends BroadcastReceiver {

    public static final String TAG = YunBaMessageReceiver.class.getSimpleName();

    private  ComingVo comingVo;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);
            Log.i(TAG,"YunBaMessageReceiver-msg:"+msg);
            Log.i(TAG,"YunBaMessageReceiver-topic:"+topic);
            LocPushBean locPushBean = null;
            try {
                locPushBean = new Gson().fromJson(msg,
                        LocPushBean.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            if(null != locPushBean){
                NotificationHelper.getInstance().showNotification(context,locPushBean);
                String userId = locPushBean.getUserid();
                String shopId = locPushBean.getShopid();
                String locId = locPushBean.getLocid();
                String userName = locPushBean.getUsername();
                String locDesc = locPushBean.getLocdesc();
                comingVo = new ComingVo();
                comingVo.setLocId(locId);
                comingVo.setUserId(userId);
                comingVo.setArriveTime(System.currentTimeMillis());
                if(!TextUtils.isEmpty(locDesc)){
                    comingVo.setLocation(locDesc);
                }
                if(!TextUtils.isEmpty(userName)){
                    comingVo.setUserName(userName);
                }
                if(!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(shopId)){
                    NetRequest netRequest = new NetRequest(ProtocolUtil.getSempNoticeUrl());
                    HashMap<String,String> bizMap = new HashMap<String,String>();
                    bizMap.put("salesid", CacheUtil.getInstance().getUserId());
                    bizMap.put("token", CacheUtil.getInstance().getToken());
                    bizMap.put("uid", userId);
                    bizMap.put("shopid", shopId);
                    netRequest.setBizParamMap(bizMap);
                    NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
                    netRequestTask.methodType = MethodType.PUSH;
                    netRequestTask.setNetRequestListener(new ExtNetRequestListener(context) {
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
                            try {
                                NoticeBean noticeBean = new Gson().fromJson(result.rawResult, NoticeBean.class);
                                if (null != noticeBean && noticeBean.isSet()) {
                                    String vip = noticeBean.getUser_level();
                                    if (!TextUtils.isEmpty(vip)) {
                                        comingVo.setVip(vip);
                                    }
                                    String phoneNum = noticeBean.getPhone();
                                    if (!TextUtils.isEmpty(phoneNum)) {
                                        comingVo.setPhoneNum(phoneNum);
                                    }
                                    BookOrderBean bookOrderBean = noticeBean.getOrder();
                                    if (null != bookOrderBean) {
                                        String roomType = bookOrderBean.getRoomType();
                                        if (!TextUtils.isEmpty(roomType)) {
                                            comingVo.setRoomType(roomType);
                                        }
                                        String checkInDate = bookOrderBean.getArrivalDate();
                                        String checkOutDate = bookOrderBean.getDepartureDate();
                                        if (!TextUtils.isEmpty(checkInDate)) {
                                            comingVo.setCheckInDate(checkInDate);
                                        }
                                        if (!TextUtils.isEmpty(checkOutDate)) {
                                            comingVo.setCheckOutDate(checkOutDate);
                                        }
                                        String orderStatus = bookOrderBean.getStatus();
                                        if (!TextUtils.isEmpty(orderStatus)) {
                                            comingVo.setOrderStatus(Integer.parseInt(orderStatus));
                                        }
                                        if (!TextUtils.isEmpty(checkInDate) && !TextUtils.isEmpty(checkOutDate)) {
                                            int stayDays = TimeUtil.daysBetween(checkInDate, checkOutDate);
                                            comingVo.setStayDays(stayDays);
                                        }
                                    }
                                    if (null != comingVo) {
                                        ComingDBUtil.getInstance().addComing(comingVo);
                                        Intent i = new Intent();
                                        i.setAction(Constants.ACTION_NOTICE);
                                        i.putExtra("comingVo",comingVo);
                                        context.sendBroadcast(i);
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }

                        @Override
                        public void beforeNetworkRequestStart() {

                        }
                    });
                    netRequestTask.isShowLoadingDialog = false;
                    netRequestTask.execute();
                }
            }

        } else if(YunBaManager.PRESENCE_RECEIVED_ACTION.equals(intent.getAction())) {
            //msg from presence.
            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String payload = intent.getStringExtra(YunBaManager.MQTT_MSG);
            StringBuilder showMsg = new StringBuilder();
            showMsg.append("Received message presence: ").append(YunBaManager.MQTT_TOPIC)
                    .append(" = ").append(topic).append(" ")
                    .append(YunBaManager.MQTT_MSG).append(" = ").append(payload);
            Log.d(TAG, showMsg.toString());
        }
    }
}
