package com.zkjinshi.superservice.fragment;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.notice.OnlineListActivity;
import com.zkjinshi.superservice.adapter.LocMoreAdapter;
import com.zkjinshi.superservice.adapter.LocNotificationAdapter;
import com.zkjinshi.superservice.bean.BookOrderBean;
import com.zkjinshi.superservice.bean.NoticeBean;
import com.zkjinshi.superservice.entity.MsgEmpStatus;
import com.zkjinshi.superservice.entity.MsgEmpStatusCount;
import com.zkjinshi.superservice.entity.MsgEmpStatusCountRSP;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestListener;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.sqlite.ComingDBUtil;
import com.zkjinshi.superservice.sqlite.ShopEmployeeDBUtil;
import com.zkjinshi.superservice.sqlite.ZoneDBUtil;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.view.CircleStatusView;
import com.zkjinshi.superservice.view.RecyclerViewHeader;
import com.zkjinshi.superservice.vo.ComingVo;
import com.zkjinshi.superservice.entity.MsgPushTriggerLocNotificationM2S;
import com.zkjinshi.superservice.vo.IdentityType;
import com.zkjinshi.superservice.vo.ZoneVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
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

    public static final int NOTICE_PAGE_SIZE = 5;
    public static final int REQUEST_PAGE_SIZE = 10;

    private Activity activity;
    private RecyclerView notityRecyclerView,moreRecyclerView;
    private LinearLayoutManager notifyLayoutManager;
    private LinearLayoutManager moreLayoutManager;
    private LocNotificationAdapter mNotificationAdapter;
    private LocMoreAdapter locMoreAdapter;
    private CircleStatusView moreStatsuView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ComingVo comingVo;
    private ArrayList<ComingVo> notifyComingList = new ArrayList<ComingVo>();
    private ArrayList<ComingVo> moreComingList = new ArrayList<ComingVo>();
    private ArrayList<ComingVo> requestComingList = new ArrayList<ComingVo>();
    private TextView totoalEmployeeTv,currentOnlineEmployeeTv;
    private LinearLayout onlineLayout;
    private int totalEmpCount,onlineEmpCout;
    private RecyclerViewHeader recyclerViewHeader;

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
        recyclerViewHeader = RecyclerViewHeader.fromXml(getContext(), R.layout.layout_online_stat);
        totoalEmployeeTv = (TextView)recyclerViewHeader.findViewById(R.id.notice_tv_total_employee);
        currentOnlineEmployeeTv = (TextView)recyclerViewHeader.findViewById(R.id.notice_tv_current_online_employee);
        onlineLayout = (LinearLayout)recyclerViewHeader.findViewById(R.id.online_layout);
        mNotificationAdapter = new LocNotificationAdapter(activity, notifyComingList);
        notityRecyclerView.setAdapter(mNotificationAdapter);
        notityRecyclerView.setHasFixedSize(true);
        notifyLayoutManager = new LinearLayoutManager(activity);
        notifyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notityRecyclerView.setLayoutManager(notifyLayoutManager);
        recyclerViewHeader.attachTo(notityRecyclerView);
        locMoreAdapter = new LocMoreAdapter(activity, moreComingList);
        moreLayoutManager = new LinearLayoutManager(activity);
        moreLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        moreRecyclerView.setLayoutManager(moreLayoutManager);
        moreRecyclerView.setAdapter(locMoreAdapter);
        moreStatsuView.setStatus(CircleStatusView.CircleStatus.STATUS_MORE);
        moreStatsuView.invalidate();
        totalEmpCount = ShopEmployeeDBUtil.getInstance().queryTotalEmpCount(CacheUtil.getInstance().getShopID());
        totoalEmployeeTv.setText("/" + totalEmpCount);
        queryPageMessages(REQUEST_PAGE_SIZE, System.currentTimeMillis(), true);
        requestOnlineCountTask();
        if(CacheUtil.getInstance().getLoginIdentity()== IdentityType.WAITER){
            onlineLayout.setVisibility(View.GONE);
        }else{
            onlineLayout.setVisibility(View.VISIBLE);
        }

    }

    private void initListeners(){

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notifyComingList = new ArrayList<ComingVo>();
                queryPageMessages(REQUEST_PAGE_SIZE, System.currentTimeMillis(), true);
            }
        });

        //自动加载更多
        notityRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        //加载更多功能的代码
                        ComingVo comingVo = notifyComingList.get(notifyComingList.size()-1);
                        long arriveTime = comingVo.getArriveTime();
                        queryPageMessages(REQUEST_PAGE_SIZE, arriveTime, false);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if (dy > 0) {
                    //大于0表示，正在向下滚动
                    isSlidingToLast = true;
                } else {
                    //小于等于0 表示停止或向下滚动
                    isSlidingToLast = false;
                }
            }
        });

        //在线布局栏
        onlineLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OnlineListActivity.class);
                intent.putExtra("onlineEmpCout",onlineEmpCout);
                intent.putExtra("totalEmpCount",totalEmpCount);
                startActivity(intent);
            }
        });

    }

    private void addObservers() {
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_PushTriggerLocNotification_M2S);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_ShopEmpStatusCount_RSP);
    }

    private void removeObservers(){
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_PushTriggerLocNotification_M2S);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_ShopEmpStatusCount_RSP);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addObservers();
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
        removeObservers();
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
                            netRequestTask.methodType = MethodType.PUSH;
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
                                                notifyComingList.add(comingVo);
                                                mNotificationAdapter.setComingList(notifyComingList);
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

                }
            }else if(type == ProtocolMSG.MSG_ShopEmpStatusCount_RSP){//在线/离线和上下班状态统计
                if(null == gson){
                    gson = new Gson();
                }
                MsgEmpStatusCountRSP msgEmpStatusCountRSP = gson.fromJson(message, MsgEmpStatusCountRSP.class);
                onlineEmpCout= msgEmpStatusCountRSP.getOnlinecount();
                currentOnlineEmployeeTv.setText(""+onlineEmpCout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void queryPageMessages(final int limitSize, final long lastSendTime, final boolean isFirstTime) {
        if (null != swipeRefreshLayout) {
            swipeRefreshLayout.setRefreshing(true);
        }
        int  localCount;
        requestComingList = new ArrayList<ComingVo>();
        requestComingList = ComingDBUtil.getInstance().queryComingList(lastSendTime,limitSize,isFirstTime);
        localCount = requestComingList.size();
        if (localCount > 0) {
            if (null == notifyComingList) {
                notifyComingList = new ArrayList<>();
            }
            if (requestComingList.size() > NOTICE_PAGE_SIZE) {
                moreComingList = appendRange(requestComingList,NOTICE_PAGE_SIZE,requestComingList.size());
                removeRange(requestComingList, NOTICE_PAGE_SIZE, requestComingList.size());
            }else{
                moreComingList = new ArrayList<ComingVo>();
            }
            notifyComingList.addAll(requestComingList);
            mNotificationAdapter.setComingList(notifyComingList);
            locMoreAdapter.setComingList(moreComingList);
        }
        if (null != swipeRefreshLayout) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void removeRange(ArrayList<ComingVo> requestMessageList,
                            int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; i++) {
            requestMessageList.remove(fromIndex);
        }
    }

    public ArrayList<ComingVo> appendRange(ArrayList<ComingVo> requestMessageList,
                            int fromIndex, int toIndex) {
        moreComingList =  new ArrayList<ComingVo>();
        for (int i = fromIndex; i < toIndex; i++) {
            moreComingList.add(requestMessageList.get(i));
        }
        return moreComingList;
    }

    /**
     * 获取在线人数
     */
    private void requestOnlineCountTask(){
        MsgEmpStatusCount msgEmpStatusCount = new MsgEmpStatusCount();
        msgEmpStatusCount.setType(ProtocolMSG.MSG_ShopEmpStatusCount);
        msgEmpStatusCount.setTimestamp(System.currentTimeMillis());
        msgEmpStatusCount.setShopid(CacheUtil.getInstance().getShopID());
        String jsonMsgEmpStatusCount = new Gson().toJson(msgEmpStatusCount);
        WebSocketManager.getInstance().sendMessage(jsonMsgEmpStatusCount);
    }

}