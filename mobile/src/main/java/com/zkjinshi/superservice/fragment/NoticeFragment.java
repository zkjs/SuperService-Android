package com.zkjinshi.superservice.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.LocMoreAdapter;
import com.zkjinshi.superservice.adapter.LocNotificationAdapter;
import com.zkjinshi.superservice.bean.BookOrderBean;
import com.zkjinshi.superservice.bean.NoticeBean;
import com.zkjinshi.superservice.listener.RecyclerLoadMoreListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ComingDBUtil;
import com.zkjinshi.superservice.sqlite.ZoneDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleStatusView;
import com.zkjinshi.superservice.vo.ComingVo;
import com.zkjinshi.superservice.vo.LatestClientVo;
import com.zkjinshi.superservice.entity.MsgPushTriggerLocNotificationM2S;
import com.zkjinshi.superservice.test.LatestClientBiz;
import com.zkjinshi.superservice.vo.ZoneVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 到店通知Fragment页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NoticeFragment extends Fragment implements IMessageObserver{

    public static final String TAG = "NoticeFragment";

    private Activity activity;
    private RecyclerView notityRecyclerView,moreRecyclerView;
    private LinearLayoutManager notifyLayoutManager;
    private LinearLayoutManager moreLayoutManager;
    private LocNotificationAdapter mNotificationAdapter;
    private LocMoreAdapter locMoreAdapter;
    private CircleStatusView moreStatsuView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ComingVo comingVo;
    private ArrayList<ComingVo> comingList;

    public static NoticeFragment newInstance() {
        return new NoticeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        notityRecyclerView = (RecyclerView) view.findViewById(R.id.rcv_notice);
        moreRecyclerView = (RecyclerView)view.findViewById(R.id.rcv_more);
        moreStatsuView = (CircleStatusView)view.findViewById(R.id.notice_more_cv_status);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.srl_notice);
    }

    private void initData() {
        activity = this.getActivity();
        mNotificationAdapter = new LocNotificationAdapter(activity, comingList);
        notityRecyclerView.setAdapter(mNotificationAdapter);
        notityRecyclerView.setHasFixedSize(true);
        notifyLayoutManager = new LinearLayoutManager(activity);
        notifyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notityRecyclerView.setLayoutManager(notifyLayoutManager);

        locMoreAdapter = new LocMoreAdapter(activity,comingList);
        moreLayoutManager = new LinearLayoutManager(activity);
        moreLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        moreRecyclerView.setLayoutManager(moreLayoutManager);
        moreRecyclerView.setAdapter(locMoreAdapter);
        moreStatsuView.setStatus(CircleStatusView.CircleStatus.STATUS_MORE);
        moreStatsuView.invalidate();
    }

    private void initListeners(){

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        //自动加载更多
        mNotificationAdapter.setOnLoadMoreListener(new RecyclerLoadMoreListener() {
            @Override
            public void loadMore() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_PushTriggerLocNotification_M2S);
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        initListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO: 随机修改圆形图片背景色
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_PushTriggerLocNotification_M2S);
    }

    @Override
    public void receive(String message) {
        if(TextUtils.isEmpty(message)){
            return ;
        }
        Gson gson = null;
        try {
            JSONObject messageObj = null;
                messageObj = new JSONObject(message);
            //接收到店通知
            int type = messageObj.getInt("type");
            if (type == ProtocolMSG.MSG_PushTriggerLocNotification_M2S) {
                if(null == gson){
                    gson = new Gson();
                }
                //1、查询数据库，重新获取数据
                MsgPushTriggerLocNotificationM2S msgLocNotification = gson.fromJson(message,
                        MsgPushTriggerLocNotificationM2S.class);
                if(null != msgLocNotification){
                    String userId = msgLocNotification.getUserid();
                    String shopId = msgLocNotification.getShopid();
                    String locId = msgLocNotification.getLocid();
                    String userName = msgLocNotification.getUsername();
                    ZoneVo zoneVo = ZoneDBUtil.getInstance().queryZoneByLocId(locId);
                    if(null != zoneVo){
                        comingVo = new ComingVo();
                        comingVo.setLocId(locId);
                        comingVo.setUserId(userId);
                        comingVo.setArriveTime(System.currentTimeMillis());
                        String location = zoneVo.getLocDesc();
                        if(!TextUtils.isEmpty(location)){
                            comingVo.setLocation(location);
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
                            NetRequestTask netRequestTask = new NetRequestTask(getActivity(),netRequest, NetResponse.class);
                            netRequestTask.methodType = MethodType.POST;
                            netRequestTask.setNetRequestListener(new NetRequestListener() {
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
                            netRequestTask.isShowLoadingDialog = true;
                            netRequestTask.execute();
                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}