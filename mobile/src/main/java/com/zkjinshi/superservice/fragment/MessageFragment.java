package com.zkjinshi.superservice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.zkjinshi.base.net.observer.IMessageObserver;
import com.zkjinshi.base.net.observer.MessageSubject;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.ChatActivity;
import com.zkjinshi.superservice.adapter.MessageAdapter;
import com.zkjinshi.superservice.entity.MsgUserDefine;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.sqlite.MessageDBUtil;
import com.zkjinshi.superservice.vo.MessageVo;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 消息通知Fragment页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageFragment extends Fragment  implements IMessageObserver {

    private RecyclerView messageRCV;
    private MessageAdapter messageAdapter;
    private ArrayList<MessageVo> messageList;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    private void initView(View view){
        messageRCV = (RecyclerView)view.findViewById(R.id.rcv_message);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.srl_message);
    }

    /**
     * 添加EventBus消息通知观察者
     */
    private void addObservers() {
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceTextChat);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceMediaChat);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_CustomerServiceImgChat);
        MessageSubject.getInstance().addObserver(this, ProtocolMSG.MSG_UserDefine);
    }

    /**
     * 删除EventBus消息通知观察者
     */
    private void removeObservers() {
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceTextChat);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceMediaChat);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_CustomerServiceImgChat);
        MessageSubject.getInstance().removeObserver(this, ProtocolMSG.MSG_UserDefine);
    }

    private void initData(){
        messageList = MessageDBUtil.getInstance().queryHistoryMessageList();
        messageAdapter = new MessageAdapter(getActivity(),messageList);
        messageRCV.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messageRCV.setLayoutManager(linearLayoutManager);
        messageRCV.setAdapter(messageAdapter);
        addObservers();
    }

    private void initListeners(){

        messageAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                MessageVo messageVo = messageList.get(postion);
                if (null != messageVo) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    String sessionId = messageVo.getSessionId();
                    String shopId = messageVo.getShopId();
                    if (!TextUtils.isEmpty(sessionId)) {
                        intent.putExtra("session_id", sessionId);
                    }
                    if (!TextUtils.isEmpty(shopId)) {
                        intent.putExtra("shop_id", shopId);
                    }
                    startActivity(intent);
                }
            }
        });

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container,
                false);
        initView(view);
        return view;
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
        messageList = MessageDBUtil.getInstance().queryHistoryMessageList();
        messageAdapter.setMessageList(messageList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeObservers();
    }

    @Override
    public void receive(String message) {
        try {
            JSONObject messageObj = new JSONObject(message);
            int type = messageObj.getInt("type");
            if(type == ProtocolMSG.MSG_CustomerServiceTextChat || type == ProtocolMSG.MSG_CustomerServiceMediaChat || type == ProtocolMSG.MSG_CustomerServiceImgChat){
                messageList = MessageDBUtil.getInstance().queryHistoryMessageList();
                messageAdapter.setMessageList(messageList);
            }

            if(type == ProtocolMSG.MSG_UserDefine){
                Gson gson = new Gson();
                MsgUserDefine msgUserDefine = gson.fromJson(message, MsgUserDefine.class);
                if(msgUserDefine.getChildtype() == ProtocolMSG.MSG_ChildType_BindInviteCode){
                    //在消息中心中展示邀请码绑定消息
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
