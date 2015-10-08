package com.zkjinshi.superservice.fragment;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.LocMoreAdapter;
import com.zkjinshi.superservice.adapter.LocNotificationAdapter;
import com.zkjinshi.superservice.listener.RecyclerLoadMoreListener;
import com.zkjinshi.superservice.view.CircleStatusView;
import com.zkjinshi.superservice.vo.LatestClientVo;
import com.zkjinshi.superservice.entity.MsgPushTriggerLocNotificationM2S;
import com.zkjinshi.superservice.sqlite.LatestClientDBUtil;
import com.zkjinshi.superservice.test.LatestClientBiz;
import com.zkjinshi.superservice.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 到店通知Fragment页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NoticeFragment extends Fragment implements IMessageObserver{

    private final static int REFRESH_UI = 0x00;

    private Activity mActivity;
    private RecyclerView mRcvNotice,moreRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private LinearLayoutManager moreLayoutManager;
    private ArrayList<LatestClientVo> mLatestClients;
    private LocNotificationAdapter mNotificationAdapter;
    private LocMoreAdapter locMoreAdapter;
    private CircleStatusView moreStatsuView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static NoticeFragment newInstance() {
        return new NoticeFragment();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_UI :
                    LatestClientVo clientBean = (LatestClientVo) msg.obj;
                    if(null != clientBean){
                        mLatestClients.add(clientBean);
                        mNotificationAdapter.setData(mLatestClients);
                    }
                break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        mRcvNotice      = (RecyclerView) view.findViewById(R.id.rcv_notice);
        moreRecyclerView = (RecyclerView)view.findViewById(R.id.rcv_more);
        moreStatsuView = (CircleStatusView)view.findViewById(R.id.notice_more_cv_status);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.srl_notice);
    }

    private void initData() {
        mActivity = this.getActivity();
        mLatestClients       = LatestClientBiz.getLatestClients();
        mNotificationAdapter = new LocNotificationAdapter(mActivity, mLatestClients);
        mRcvNotice.setAdapter(mNotificationAdapter);
        mRcvNotice.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvNotice.setLayoutManager(mLayoutManager);

        locMoreAdapter = new LocMoreAdapter(mActivity,mLatestClients);
        moreLayoutManager = new LinearLayoutManager(mActivity);
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
                String userID = msgLocNotification.getUserid();
                if(!TextUtils.isEmpty(userID)){
                    LatestClientVo clientBean = LatestClientDBUtil.getInstance().queryLatestClientByUserID(userID);
                    //2、根据查询数据刷新界面
                    Message msg = Message.obtain();
                    msg.what    = REFRESH_UI;
                    msg.obj     = clientBean;
                    handler.sendMessage(msg);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}