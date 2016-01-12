package com.zkjinshi.superservice.fragment;

import android.app.Activity;
import android.os.Bundle;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.LocNotificationAdapter;
import com.zkjinshi.superservice.net.ExtNetRequestListener;
import com.zkjinshi.superservice.net.MethodType;
import com.zkjinshi.superservice.net.NetRequest;
import com.zkjinshi.superservice.net.NetRequestTask;
import com.zkjinshi.superservice.net.NetResponse;
import com.zkjinshi.superservice.utils.CacheUtil;
import com.zkjinshi.superservice.utils.ProtocolUtil;
import com.zkjinshi.superservice.vo.NoticeVo;

import org.jivesoftware.smack.util.Base64Encoder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 到店通知Fragment页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NoticeFragment extends Fragment{

    public static final String TAG = "NoticeFragment";
    private Activity activity;
    private RecyclerView notityRecyclerView;
    private LinearLayoutManager notifyLayoutManager;
    private LocNotificationAdapter notificationAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<NoticeVo> noticeList = new ArrayList<NoticeVo>();
    private TextView emptyTips;

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
        emptyTips = (TextView) view.findViewById(R.id.empty_tips);
        emptyTips.setText("暂无到店通知");
        notityRecyclerView = (RecyclerView) view.findViewById(R.id.rcv_notice);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.srl_notice);
    }

    private void initData() {

        activity = this.getActivity();

        notificationAdapter = new LocNotificationAdapter(activity, noticeList);
        notityRecyclerView.setAdapter(notificationAdapter);
        notityRecyclerView.setHasFixedSize(true);
        notityRecyclerView.setNestedScrollingEnabled(false);
        notifyLayoutManager = new LinearLayoutManager(activity);
        notifyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        notityRecyclerView.setLayoutManager(notifyLayoutManager);
        requestNoticesTask();
    }

    private void initListeners(){

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                noticeList = new ArrayList<NoticeVo>();
                requestNoticesTask();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        requestNoticesTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 请求到店通知
     */
    private void requestNoticesTask(){
        String locIds = CacheUtil.getInstance().getAreaInfo();
        if(TextUtils.isEmpty(locIds)){
            try {
                if(null != swipeRefreshLayout){
                    swipeRefreshLayout.setRefreshing(false);
                }
                noticeList = new ArrayList<NoticeVo>();
                notificationAdapter.setNoticeList(noticeList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if(locIds.contains(",")){
             mutileZoneNotice();
        }else{
            singleZoneNotice();
        }

    }

    /**
     * 获取单个到店区域通知
     */
    public void singleZoneNotice(){
        String locIds = CacheUtil.getInstance().getAreaInfo();
        String token = CacheUtil.getInstance().getToken();
        String shopId = CacheUtil.getInstance().getShopID();
        String noticesUrl = ProtocolUtil.getNoticeUrl(shopId,locIds,token);
        NetRequest netRequest = new NetRequest(noticesUrl);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(), netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;

        netRequestTask.setNetRequestListener(new ExtNetRequestListener(getActivity()) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                if(null != swipeRefreshLayout){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onNetworkRequestCancelled() {
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                try {
                    if(null != swipeRefreshLayout){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    noticeList = new Gson().fromJson(result.rawResult, new TypeToken< ArrayList<NoticeVo>>(){}.getType());
                    notificationAdapter.setNoticeList(noticeList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

    /**
     * 获取多个到店区域通知
     */
    public void mutileZoneNotice(){
        String locIds = CacheUtil.getInstance().getAreaInfo();
        String shopId = CacheUtil.getInstance().getShopID();
        String noticesUrl =  ConfigUtil.getInst().getJavaDomain()+"arrive/users";

        NetRequest netRequest = new NetRequest(noticesUrl);
        HashMap<String, String> bigMap = new HashMap<>();
        bigMap.put("shopid",shopId);
        bigMap.put("locid",locIds);
        netRequest.setBizParamMap(bigMap);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(), netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSON;

        netRequestTask.setNetRequestListener(new ExtNetRequestListener(getActivity()) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                if(null != swipeRefreshLayout){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onNetworkRequestCancelled() {
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                try {
                    if(null != swipeRefreshLayout){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    noticeList = new Gson().fromJson(result.rawResult, new TypeToken< ArrayList<NoticeVo>>(){}.getType());
                    notificationAdapter.setNoticeList(noticeList);
                } catch (Exception e) {
                    e.printStackTrace();
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