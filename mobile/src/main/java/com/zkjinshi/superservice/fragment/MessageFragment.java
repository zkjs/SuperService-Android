package com.zkjinshi.superservice.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMConversation;
import com.zkjinshi.superservice.R;
import com.zkjinshi.superservice.activity.chat.ChatActivity;
import com.zkjinshi.superservice.adapter.MessageAdapter;
import com.zkjinshi.superservice.emchat.EMConversationHelper;
import com.zkjinshi.superservice.emchat.observer.EMessageSubject;
import com.zkjinshi.superservice.emchat.observer.IEMessageObserver;
import com.zkjinshi.superservice.listener.RecyclerItemClickListener;
import com.zkjinshi.superservice.utils.Constants;

import java.util.ArrayList;

/**
 * 消息通知Fragment页面
 * 开发者：JimmyZhang
 * 日期：2015/9/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageFragment extends Fragment implements IEMessageObserver {

    private RecyclerView messageRCV;
    private MessageAdapter messageAdapter;
    private ArrayList<EMConversation> conversationList;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    private void initView(View view){
        messageRCV = (RecyclerView)view.findViewById(R.id.rcv_message);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.srl_message);
    }

    private void initData(){
        messageAdapter = new MessageAdapter(getActivity(),conversationList);
        messageRCV.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messageRCV.setLayoutManager(linearLayoutManager);
        messageRCV.setAdapter(messageAdapter);
    }

    private void initListeners() {

        messageAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                EMConversation conversation = conversationList.get(position);
                String username = conversation.getUserName();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                if (conversation.isGroup()) {
                    if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
                        intent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_CHATROOM);
                    } else {
                        intent.putExtra(Constants.EXTRA_CHAT_TYPE, Constants.CHATTYPE_GROUP);
                    }

                }
                intent.putExtra(Constants.EXTRA_USER_ID, username);
                startActivity(intent);
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
        addAllObserver();
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
        conversationList = (ArrayList<EMConversation>) EMConversationHelper.getInstance().loadConversationList();
        messageAdapter.setConversationList(conversationList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeAllObserver();
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conversationList.clear();
                conversationList.addAll(EMConversationHelper.getInstance().loadConversationList());
                messageAdapter.setConversationList(conversationList);
            }
        });
    }

    /**
     * 添加观察者
     */
    private void addAllObserver(){
        EMessageSubject.getInstance().addObserver(this,EMNotifierEvent.Event.EventNewMessage);
        EMessageSubject.getInstance().addObserver(this,EMNotifierEvent.Event.EventOfflineMessage);
        EMessageSubject.getInstance().addObserver(this,EMNotifierEvent.Event.EventConversationListChanged);
    }

    /**
     * 移除观察者
     */
    private void removeAllObserver(){
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventNewMessage);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventOfflineMessage);
        EMessageSubject.getInstance().removeObserver(this, EMNotifierEvent.Event.EventConversationListChanged);
    }

    @Override
    public void receive(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
            case EventOfflineMessage:
            case EventConversationListChanged:
                refresh();
                break;
            default:
                break;
        }
    }
}
