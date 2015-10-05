package com.zkjinshi.superservice.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.adapter.LocNotificationAdapter;
import com.zkjinshi.superservice.vo.LocNotificationVo;

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

    private Activity     mActivity;
    private RecyclerView mRcvNotice;

    private RecyclerView.LayoutManager mLayoutManager;

    private List<LocNotificationVo>    mNotifications;
    private LocNotificationAdapter     mNotificationAdapter;

    public static NoticeFragment newInstance() {
        return new NoticeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view){
        mRcvNotice = (RecyclerView) view.findViewById(R.id.rcv_notice);
    }

    private void initData() {

        mActivity = this.getActivity();
        //1. 伪造到店通知数据
        mNotifications = new ArrayList<>();
        mNotifications.addAll(getNotifications());
        mNotificationAdapter = new LocNotificationAdapter(mActivity, mNotifications);
        mRcvNotice.setAdapter(mNotificationAdapter);
        mRcvNotice.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mRcvNotice.setLayoutManager(mLayoutManager);

    }

    /**
     * TODO: 伪造数据
     * @return
     */
    public List<LocNotificationVo> getNotifications(){
        List<LocNotificationVo> notifications = new ArrayList<>();
        for(int i=0; i< 10; i++){
            LocNotificationVo notification = new LocNotificationVo();
            notification.setTimestamp(System.currentTimeMillis());
            notification.setShopid("120");
            notification.setUserid(System.currentTimeMillis() + "");
            notification.setUsername(System.currentTimeMillis() + "Sir");
            notification.setTimestamp(new Random().nextInt(10));
            notifications.add(notification);
        }
        return notifications;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_PushTriggerLocNotification_M2S);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_PushTriggerLocNotification_M2S);
    }

    @Override
    public void receive(String message) {
        //TODO JimmyZhang接收到店通知
        //TODO JimmyZhang 1、查询数据库，重新获取数据
        //TODO JimmyZhang 2、根据查询数据刷新界面
    }
}
